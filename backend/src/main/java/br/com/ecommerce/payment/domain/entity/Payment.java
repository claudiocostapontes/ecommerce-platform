package br.com.ecommerce.payment.domain.entity;

import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.order.domain.entity.Order;
import br.com.ecommerce.payment.domain.enums.PaymentMethod;
import br.com.ecommerce.payment.domain.enums.PaymentStatus;
import br.com.ecommerce.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payments_order_id", columnList = "order_id"),
    @Index(name = "idx_payments_status", columnList = "status"),
    @Index(name = "idx_payments_gateway_id", columnList = "gateway_transaction_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends AuditableEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod method;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;
    
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false, precision = 10, scale = 2))
    private Money amount;
    
    @Column(name = "gateway_transaction_id", length = 100)
    private String gatewayTransactionId;
    
    @Column(name = "gateway_name", length = 50)
    private String gatewayName;
    
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "refunded_amount", precision = 10, scale = 2))
    private Money refundedAmount;
    
    @Column(name = "payment_reference", length = 100)
    private String paymentReference;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @Column(name = "failed_at")
    private LocalDateTime failedAt;
    
    @Column(columnDefinition = "TEXT")
    private String failureReason;
    
    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;
    
    public void markAsPaid(String gatewayTransactionId) {
        this.status = PaymentStatus.PAID;
        this.gatewayTransactionId = gatewayTransactionId;
        this.paidAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.failedAt = LocalDateTime.now();
    }
    
    public void markAsDeclined(String reason) {
        this.status = PaymentStatus.DECLINED;
        this.failureReason = reason;
        this.failedAt = LocalDateTime.now();
    }
    
    public boolean canBeRefunded() {
        return status == PaymentStatus.PAID || status == PaymentStatus.AUTHORIZED;
    }
    
    public void refund(Money refundAmount) {
        if (!canBeRefunded()) {
            throw new IllegalStateException("Payment cannot be refunded in " + status + " status");
        }
        
        Money currentRefund = refundedAmount != null ? refundedAmount : Money.zero();
        Money totalRefund = currentRefund.add(refundAmount);
        
        if (totalRefund.isGreaterThan(amount)) {
            throw new IllegalArgumentException("Refund amount exceeds payment amount");
        }
        
        this.refundedAmount = totalRefund;
        
        if (totalRefund.equals(amount)) {
            this.status = PaymentStatus.REFUNDED;
        } else {
            this.status = PaymentStatus.PARTIALLY_REFUNDED;
        }
    }
}