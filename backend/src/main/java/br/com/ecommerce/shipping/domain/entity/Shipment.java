package br.com.ecommerce.shipping.domain.entity;

import br.com.ecommerce.order.domain.entity.Order;
import br.com.ecommerce.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments", indexes = {
    @Index(name = "idx_shipments_order_id", columnList = "order_id"),
    @Index(name = "idx_shipments_tracking_code", columnList = "tracking_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends AuditableEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;
    
    @Column(name = "tracking_code", unique = true, length = 100)
    private String trackingCode;
    
    @Column(name = "carrier", nullable = false, length = 50)
    private String carrier;
    
    @Column(name = "shipping_method", nullable = false, length = 100)
    private String shippingMethod;
    
    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;
    
    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;
    
    @Column(name = "actual_delivery")
    private LocalDateTime actualDelivery;
    
    @Column(name = "sender_name", length = 150)
    private String senderName;
    
    @Column(name = "sender_address", columnDefinition = "TEXT")
    private String senderAddress;
    
    @Column(name = "recipient_name", length = 150)
    private String recipientName;
    
    @Column(name = "recipient_address", columnDefinition = "TEXT")
    private String recipientAddress;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
}