package br.com.ecommerce.order.domain.entity;

import br.com.ecommerce.auth.domain.entity.User;
import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.order.domain.enums.OrderStatus;
import br.com.ecommerce.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_user_id", columnList = "user_id"),
    @Index(name = "idx_orders_status", columnList = "status"),
    @Index(name = "idx_orders_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AuditableEntity {
    
    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();
    
    // Pricing
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "subtotal", nullable = false, precision = 10, scale = 2))
    private Money subtotal;
    
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "discount", precision = 10, scale = 2))
    private Money discount;
    
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "shipping_cost", nullable = false, precision = 10, scale = 2))
    private Money shippingCost;
    
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "total", nullable = false, precision = 10, scale = 2))
    private Money total;
    
    // Delivery
    @Column(name = "delivery_address", columnDefinition = "TEXT")
    private String deliveryAddress;
    
    @Column(name = "delivery_city", length = 100)
    private String deliveryCity;
    
    @Column(name = "delivery_state", length = 50)
    private String deliveryState;
    
    @Column(name = "delivery_zip_code", length = 20)
    private String deliveryZipCode;
    
    @Column(name = "delivery_recipient_name", length = 150)
    private String deliveryRecipientName;
    
    @Column(name = "delivery_phone", length = 20)
    private String deliveryPhone;
    
    // Tracking
    @Column(name = "tracking_code", length = 100)
    private String trackingCode;
    
    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    // Notes
    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;
    
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
    
    public void changeStatus(OrderStatus newStatus, String reason) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Cannot transition from " + status + " to " + newStatus);
        }
        
        this.status = newStatus;
        
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(this)
                .previousStatus(this.status)
                .newStatus(newStatus)
                .reason(reason)
                .build();
        
        this.statusHistory.add(history);
    }
    
    public Money getTotal() {
        return subtotal.subtract(discount != null ? discount : Money.zero())
                      .add(shippingCost != null ? shippingCost : Money.zero());
    }
}
