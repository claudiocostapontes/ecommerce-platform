package br.com.ecommerce.payment.application.service;

import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.order.domain.entity.Order;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final List<PaymentGateway> paymentGateways;
    
    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        // Check idempotency
        if (request.idempotencyKey() != null) {
            var existingPayment = paymentRepository.findByIdempotencyKey(request.idempotencyKey());
            if (existingPayment.isPresent()) {
                log.warn("Duplicate payment request with idempotency key: {}", request.idempotencyKey());
                return toResponse(existingPayment.get());
            }
        }
        
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.orderId()));
        
        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new BusinessException("Cannot process payment for order in " + order.getStatus() + " status");
        }
        
        // Check if payment already exists
        var existingPayment = paymentRepository.findByOrderId(request.orderId());
        if (existingPayment.isPresent() && 
            (existingPayment.get().getStatus() == PaymentStatus.PAID || 
             existingPayment.get().getStatus() == PaymentStatus.AUTHORIZED)) {
            throw new BusinessException("Payment already processed for this order");
        }
        
        // Find suitable gateway
        PaymentGateway gateway = findGateway(request.method());
        
        // Create payment record
        Payment payment = Payment.builder()
                .order(order)
                .method(request.method())
                .status(PaymentStatus.PROCESSING)
                .amount(Money.of(order.getTotal().getAmount()))
                .gatewayName(gateway.getGatewayName())
                .idempotencyKey(request.idempotencyKey())
                .metadata(request.paymentDetails())
                .build();
        
        payment = paymentRepository.save(payment);
        
        // Process with gateway
        try {
            var gatewayResponse = gateway.processPayment(request);
            
            if (gatewayResponse.success()) {
                payment.markAsPaid(gatewayResponse.transactionId());
                payment.setPaymentReference(gatewayResponse.transactionId());
                payment.setMetadata(gatewayResponse.metadata());
                
                // Update order status
                order.changeStatus(OrderStatus.PAID, "Payment processed successfully");
                orderRepository.save(order);
                
                log.info("Payment processed successfully for order: {}", order.getOrderNumber());
            } else {
                payment.markAsFailed(gatewayResponse.message());
                order.changeStatus(OrderStatus.PAYMENT_FAILED, gatewayResponse.message());
                orderRepository.save(order);
                
                log.warn("Payment failed for order: {}", order.getOrderNumber());
                throw new BusinessException("Payment processing failed: " + gatewayResponse.message());
            }
        } catch (Exception e) {
            payment.markAsFailed(e.getMessage());
            order.changeStatus(OrderStatus.PAYMENT_FAILED, e.getMessage());
            orderRepository.save(order);
            
            log.error("Error processing payment for order: {}", order.getOrderNumber(), e);
            throw new BusinessException("Error processing payment: " + e.getMessage());
        }
        
        payment = paymentRepository.save(payment);
        return toResponse(payment);
    }
    
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        return toResponse(payment);
    }
    
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrder(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
        return toResponse(payment);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        Page<Payment> page = paymentRepository.findByStatus(status, pageable);
        Page<PaymentResponse> dtoPage = page.map(this::toResponse);
        return PageResponse.of(dtoPage);
    }
    
    @Transactional
    public PaymentResponse refundPayment(UUID paymentId, RefundPaymentRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        
        if (!payment.canBeRefunded()) {
            throw new BusinessException("Payment cannot be refunded in " + payment.getStatus() + " status");
        }
        
        Money refundAmount = Money.of(request.amount());
        
        // Find gateway
        PaymentGateway gateway = findGatewayByName(payment.getGatewayName());
        
        try {
            var gatewayResponse = gateway.refundPayment(payment.getGatewayTransactionId(), request.amount());
            
            if (gatewayResponse.success()) {
                payment.refund(refundAmount);
                
                log.info("Refund processed for payment: {}", paymentId);
            } else {
                throw new BusinessException("Refund failed: " + gatewayResponse.message());
            }
        } catch (Exception e) {
            log.error("Error processing refund for payment: {}", paymentId, e);
            throw new BusinessException("Error processing refund: " + e.getMessage());
        }
        
        payment = paymentRepository.save(payment);
        return toResponse(payment);
    }
    
    private PaymentGateway findGateway(PaymentMethod method) {
        return paymentGateways.stream()
                .filter(gateway -> gateway.supportsMethod(method))
                .findFirst()
                .orElseThrow(() -> new BusinessException("No payment gateway available for method: " + method));
    }
    
    private PaymentGateway findGatewayByName(String gatewayName) {
        return paymentGateways.stream()
                .filter(gateway -> gateway.getGatewayName().equals(gatewayName))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Gateway not found: " + gatewayName));
    }
    
    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount().getAmount())
                .refundedAmount(payment.getRefundedAmount() != null ? payment.getRefundedAmount().getAmount() : null)
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .paymentReference(payment.getPaymentReference())
                .paidAt(payment.getPaidAt())
                .failedAt(payment.getFailedAt())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}