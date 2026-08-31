package br.com.ecommerce.payment.application.service;

import br.com.ecommerce.auth.domain.entity.User;
import br.com.ecommerce.auth.domain.repository.UserRepository;
import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.inventory.application.service.InventoryService;
import br.com.ecommerce.order.domain.entity.Order;
import br.com.ecommerce.order.domain.entity.OrderItem;
import br.com.ecommerce.order.domain.enums.OrderStatus;
import br.com.ecommerce.order.domain.repository.OrderRepository;
import br.com.ecommerce.payment.application.dto.PaymentResponse;
import br.com.ecommerce.payment.application.dto.ProcessPaymentRequest;
import br.com.ecommerce.payment.application.dto.RefundPaymentRequest;
import br.com.ecommerce.payment.domain.entity.Payment;
import br.com.ecommerce.payment.domain.enums.PaymentMethod;
import br.com.ecommerce.payment.domain.enums.PaymentStatus;
import br.com.ecommerce.payment.domain.repository.PaymentRepository;
import br.com.ecommerce.payment.domain.service.PaymentGateway;
import br.com.ecommerce.shared.dto.PageResponse;
import br.com.ecommerce.shared.exception.BusinessException;
import br.com.ecommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final List<PaymentGateway> paymentGateways;

    @Transactional
    public PaymentResponse processPayment(
            String username,
            ProcessPaymentRequest request
    ) {
        User user = getUser(username);

        /*
         * Idempotência.
         *
         * Se a mesma chave já foi utilizada, somente retornamos
         * o pagamento se ele pertencer ao mesmo usuário.
         */
        if (request.idempotencyKey() != null
                && !request.idempotencyKey().isBlank()) {

            var existingPayment =
                    paymentRepository.findByIdempotencyKey(
                            request.idempotencyKey()
                    );

            if (existingPayment.isPresent()) {
                Payment payment = existingPayment.get();

                validatePaymentOwnership(payment, user);

                log.warn(
                        "Duplicate payment request with idempotency key: {}",
                        request.idempotencyKey()
                );

                return toResponse(payment);
            }
        }

        Order order = orderRepository
                .findByIdWithItems(request.orderId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Order",
                                request.orderId()
                        )
                );

        validateOrderOwnership(order, user);

        /*
         * Somente pedidos nesses estados podem iniciar/repetir
         * uma tentativa de pagamento.
         */
        if (order.getStatus() != OrderStatus.CREATED
                && order.getStatus() != OrderStatus.PAYMENT_FAILED
                && order.getStatus() != OrderStatus.PAYMENT_PENDING) {

            throw new BusinessException(
                    "Cannot process payment for order in "
                            + order.getStatus()
                            + " status"
            );
        }

        /*
         * Impede novo pagamento se já existe um pagamento
         * efetivamente aprovado/autorizado para o pedido.
         */
        var existingPayment =
                paymentRepository.findByOrderId(request.orderId());

        if (existingPayment.isPresent()
                && (
                existingPayment.get().getStatus() == PaymentStatus.PAID
                        || existingPayment.get().getStatus() == PaymentStatus.AUTHORIZED
        )) {

            throw new BusinessException(
                    "Payment already processed for this order"
            );
        }

        /*
         * A máquina de estados exige:
         *
         * CREATED -> PAYMENT_PENDING -> PAID
         *
         * ou:
         *
         * PAYMENT_FAILED -> PAYMENT_PENDING -> PAID
         */
        if (order.getStatus() == OrderStatus.CREATED
                || order.getStatus() == OrderStatus.PAYMENT_FAILED) {

            order.changeStatus(
                    OrderStatus.PAYMENT_PENDING,
                    "Payment processing started"
            );

            orderRepository.save(order);
        }

        PaymentGateway gateway = findGateway(request.method());

        Payment payment = Payment.builder()
                .order(order)
                .method(request.method())
                .status(PaymentStatus.PROCESSING)
                .amount(
                        Money.of(
                                order.getTotal().getAmount()
                        )
                )
                .gatewayName(gateway.getGatewayName())
                .idempotencyKey(request.idempotencyKey())
                .metadata(request.paymentDetails())
                .build();

        payment = paymentRepository.save(payment);

        PaymentGateway.PaymentGatewayResponse gatewayResponse;

        try {
            gatewayResponse = gateway.processPayment(request);
        } catch (Exception e) {
            /*
             * Falha técnica ao falar com o gateway.
             *
             * Não confirmamos nem liberamos estoque aqui.
             * A reserva continua associada ao pedido.
             */
            payment.markAsFailed(e.getMessage());

            if (order.getStatus() == OrderStatus.PAYMENT_PENDING) {
                order.changeStatus(
                        OrderStatus.PAYMENT_FAILED,
                        "Payment gateway error: " + e.getMessage()
                );
            }

            paymentRepository.save(payment);
            orderRepository.save(order);

            log.error(
                    "Error processing payment for order: {}",
                    order.getOrderNumber(),
                    e
            );

            throw new BusinessException(
                    "Error processing payment: "
                            + e.getMessage()
            );
        }

        if (!gatewayResponse.success()) {
            payment.markAsFailed(gatewayResponse.message());

            if (order.getStatus() == OrderStatus.PAYMENT_PENDING) {
                order.changeStatus(
                        OrderStatus.PAYMENT_FAILED,
                        gatewayResponse.message()
                );
            }

            paymentRepository.save(payment);
            orderRepository.save(order);

            log.warn(
                    "Payment declined for order: {}",
                    order.getOrderNumber()
            );

            /*
             * A reserva permanece para permitir uma nova
             * tentativa de pagamento.
             */
            throw new BusinessException(
                    "Payment processing failed: "
                            + gatewayResponse.message()
            );
        }

        /*
         * Gateway aprovou o pagamento.
         *
         * Agora a reserva deixa de ser estoque reservado
         * e torna-se venda efetiva.
         */
        for (OrderItem item : order.getItems()) {
            inventoryService.confirmReservation(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    order.getId()
            );
        }

        payment.markAsPaid(gatewayResponse.transactionId());
        payment.setPaymentReference(
                gatewayResponse.transactionId()
        );
        payment.setMetadata(gatewayResponse.metadata());

        order.changeStatus(
                OrderStatus.PAID,
                "Payment processed successfully"
        );

        payment = paymentRepository.save(payment);
        orderRepository.save(order);

        log.info(
                "Payment processed successfully for order: {}",
                order.getOrderNumber()
        );

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(
            String username,
            UUID paymentId
    ) {
        User user = getUser(username);

        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Payment",
                                paymentId
                        )
                );

        validatePaymentOwnership(payment, user);

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrder(
            String username,
            UUID orderId
    ) {
        User user = getUser(username);

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Payment not found for order: "
                                        + orderId
                        )
                );

        validatePaymentOwnership(payment, user);

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPaymentsByStatus(
            PaymentStatus status,
            Pageable pageable
    ) {
        Page<Payment> page =
                paymentRepository.findByStatus(
                        status,
                        pageable
                );

        Page<PaymentResponse> dtoPage =
                page.map(this::toResponse);

        return PageResponse.of(dtoPage);
    }

    @Transactional
    public PaymentResponse refundPayment(
            UUID paymentId,
            RefundPaymentRequest request
    ) {
        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Payment",
                                paymentId
                        )
                );

        if (!payment.canBeRefunded()) {
            throw new BusinessException(
                    "Payment cannot be refunded in "
                            + payment.getStatus()
                            + " status"
            );
        }

        Money refundAmount =
                Money.of(request.amount());

        PaymentGateway gateway =
                findGatewayByName(
                        payment.getGatewayName()
                );

        try {
            var gatewayResponse =
                    gateway.refundPayment(
                            payment.getGatewayTransactionId(),
                            request.amount()
                    );

            if (!gatewayResponse.success()) {
                throw new BusinessException(
                        "Refund failed: "
                                + gatewayResponse.message()
                );
            }

            payment.refund(refundAmount);

            log.info(
                    "Refund processed for payment: {}",
                    paymentId
            );

        } catch (BusinessException e) {
            throw e;

        } catch (Exception e) {
            log.error(
                    "Error processing refund for payment: {}",
                    paymentId,
                    e
            );

            throw new BusinessException(
                    "Error processing refund: "
                            + e.getMessage()
            );
        }

        payment = paymentRepository.save(payment);

        return toResponse(payment);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User",
                                username
                        )
                );
    }

    private void validateOrderOwnership(
            Order order,
            User user
    ) {
        if (order.getUser() == null
                || !order.getUser()
                .getId()
                .equals(user.getId())) {

            throw new ResourceNotFoundException(
                    "Order",
                    order.getId()
            );
        }
    }

    private void validatePaymentOwnership(
            Payment payment,
            User user
    ) {
        Order order = payment.getOrder();

        if (order == null
                || order.getUser() == null
                || !order.getUser()
                .getId()
                .equals(user.getId())) {

            throw new ResourceNotFoundException(
                    "Payment",
                    payment.getId()
            );
        }
    }

    private PaymentGateway findGateway(
            PaymentMethod method
    ) {
        return paymentGateways.stream()
                .filter(
                        gateway ->
                                gateway.supportsMethod(method)
                )
                .findFirst()
                .orElseThrow(
                        () -> new BusinessException(
                                "No payment gateway available for method: "
                                        + method
                        )
                );
    }

    private PaymentGateway findGatewayByName(
            String gatewayName
    ) {
        return paymentGateways.stream()
                .filter(
                        gateway ->
                                gateway.getGatewayName()
                                        .equals(gatewayName)
                )
                .findFirst()
                .orElseThrow(
                        () -> new BusinessException(
                                "Gateway not found: "
                                        + gatewayName
                        )
                );
    }

    private PaymentResponse toResponse(
            Payment payment
    ) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(
                        payment.getOrder().getId()
                )
                .method(payment.getMethod())
                .status(payment.getStatus())
                .amount(
                        payment.getAmount().getAmount()
                )
                .refundedAmount(
                        payment.getRefundedAmount() != null
                                ? payment.getRefundedAmount()
                                .getAmount()
                                : null
                )
                .gatewayTransactionId(
                        payment.getGatewayTransactionId()
                )
                .paymentReference(
                        payment.getPaymentReference()
                )
                .paidAt(payment.getPaidAt())
                .failedAt(payment.getFailedAt())
                .failureReason(
                        payment.getFailureReason()
                )
                .createdAt(payment.getCreatedAt())
                .build();
    }
}