package br.com.ecommerce.order.application.service;

import br.com.ecommerce.auth.domain.entity.User;
import br.com.ecommerce.auth.domain.repository.UserRepository;
import br.com.ecommerce.cart.domain.entity.Cart;
import br.com.ecommerce.cart.domain.entity.CartItem;
import br.com.ecommerce.cart.domain.repository.CartRepository;
import br.com.ecommerce.cart.application.service.CartService;
import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.coupon.domain.entity.Coupon;
import br.com.ecommerce.coupon.domain.repository.CouponRepository;
import br.com.ecommerce.inventory.application.service.InventoryService;
import br.com.ecommerce.inventory.application.dto.StockReservationRequest;
import br.com.ecommerce.order.application.dto.CreateOrderRequest;
import br.com.ecommerce.order.application.dto.OrderResponse;
import br.com.ecommerce.order.application.dto.UpdateOrderStatusRequest;
import br.com.ecommerce.order.domain.entity.Order;
import br.com.ecommerce.order.domain.entity.OrderItem;
import br.com.ecommerce.order.domain.enums.OrderStatus;
import br.com.ecommerce.order.domain.repository.OrderRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final InventoryService inventoryService;
    private final CartService cartService;
    
    @Transactional
    public OrderResponse createOrder(String username, CreateOrderRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Cart cart = cartRepository.findByIdWithItems(request.cartId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", request.cartId()));
        
        if (cart.isEmpty()) {
            throw new BusinessException("Cannot create order from empty cart");
        }
        
        // Validate cart
        cartService.validateCart(request.cartId());
        
        // Calculate totals
        Money subtotal = cart.getSubtotal();
        Money discount = Money.zero();
        
        if (request.couponCode() != null && !request.couponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCode(request.couponCode())
                    .orElseThrow(() -> new BusinessException("Invalid coupon code"));
            
            if (!coupon.isValid()) {
                throw new BusinessException("Coupon is expired or inactive");
            }
            
            discount = coupon.calculateDiscount(subtotal);
        }
        
        Money shippingCost = Money.of(BigDecimal.valueOf(15.00)); // Placeholder
        Money total = subtotal.subtract(discount).add(shippingCost);
        
        // Generate order number
        String orderNumber = generateOrderNumber();
        
        // Create order
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .user(user)
                .status(OrderStatus.CREATED)
                .subtotal(subtotal)
                .discount(discount)
                .shippingCost(shippingCost)
                .total(total)
                .deliveryAddress(request.deliveryAddress())
                .deliveryCity(request.deliveryCity())
                .deliveryState(request.deliveryState())
                .deliveryZipCode(request.deliveryZipCode())
                .deliveryRecipientName(request.deliveryRecipientName())
                .deliveryPhone(request.deliveryPhone())
                .customerNotes(request.customerNotes())
                .build();
        
        // Add items to order
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .productName(cartItem.getProduct().getName())
                    .productSku(cartItem.getProduct().getSku().getCode())
                    .build();
            
            order.addItem(orderItem);
            
            // Reserve inventory
            inventoryService.reserveStock(new StockReservationRequest(
                    cartItem.getProduct().getId(),
                    cartItem.getQuantity(),
                    order.getId(),
                    "ORDER"
            ));
        }
        
        // Save order
        order = orderRepository.save(order);
        
        // Clear cart
        cart.clear();
        cartRepository.save(cart);
        
        log.info("Order created: {} for user {}", orderNumber, username);
        
        return toResponse(order);
    }
    
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        return toResponse(order);
    }
    
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
        return toResponse(order);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Page<Order> page = orderRepository.findByUserId(user.getId(), pageable);
        Page<OrderResponse> dtoPage = page.map(this::toResponse);
        return PageResponse.of(dtoPage);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<Order> page = orderRepository.findByStatus(status, pageable);
        Page<OrderResponse> dtoPage = page.map(this::toResponse);
        return PageResponse.of(dtoPage);
    }
    
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        
        order.changeStatus(request.status(), request.reason());
        
        // If order is shipped, set estimated delivery
        if (request.status() == OrderStatus.SHIPPED) {
            order.setEstimatedDelivery(LocalDateTime.now().plusDays(7));
        }
        
        // If order is delivered
        if (request.status() == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }
        
        // If order is cancelled, release inventory
        if (request.status() == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                inventoryService.releaseReservation(
                        item.getProduct().getId(),
                        item.getQuantity(),
                        order.getId()
                );
            }
        }
        
        order = orderRepository.save(order);
        log.info("Order {} status updated to {}", order.getOrderNumber(), request.status());
        
        return toResponse(order);
    }
    
    @Transactional
    public void addAdminNotes(UUID orderId, String notes) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        
        order.setAdminNotes(notes);
        orderRepository.save(order);
    }
    
    @Transactional
    public void setTrackingCode(UUID orderId, String trackingCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        
        order.setTrackingCode(trackingCode);
        orderRepository.save(order);
    }
    
    private String generateOrderNumber() {
        // Format: ORD-TIMESTAMP-RANDOM
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 10000);
        return String.format("ORD-%d-%04d", timestamp, random);
    }
    
    private OrderResponse toResponse(Order order) {
        List<OrderResponse.OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> OrderResponse.OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProductName())
                        .productSku(item.getProductSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice().getAmount())
                        .subtotal(item.getSubtotal().getAmount())
                        .build())
                .collect(Collectors.toList());
        
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .items(itemDTOs)
                .subtotal(order.getSubtotal().getAmount())
                .discount(order.getDiscount() != null ? order.getDiscount().getAmount() : BigDecimal.ZERO)
                .shippingCost(order.getShippingCost().getAmount())
                .total(order.getTotal().getAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryCity(order.getDeliveryCity())
                .deliveryState(order.getDeliveryState())
                .deliveryZipCode(order.getDeliveryZipCode())
                .trackingCode(order.getTrackingCode())
                .estimatedDelivery(order.getEstimatedDelivery())
                .deliveredAt(order.getDeliveredAt())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}