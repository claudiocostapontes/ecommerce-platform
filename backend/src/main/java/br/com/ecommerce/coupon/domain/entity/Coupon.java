package br.com.ecommerce.coupon.domain.entity;

import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.coupon.domain.enums.DiscountType;
import br.com.ecommerce.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons", indexes = {
    @Index(name = "idx_coupons_code", columnList = "code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends AuditableEntity {
    
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DiscountType discountType;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "max_discount", precision = 10, scale = 2)
    private BigDecimal maxDiscount;
    
    @Column(name = "min_purchase", precision = 10, scale = 2)
    private BigDecimal minPurchase;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;
    
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;
    
    @Column(name = "max_uses")
    private Integer maxUses;
    
    @Column(name = "current_uses")
    @Builder.Default
    private Integer currentUses = 0;
    
    @Column(name = "max_uses_per_customer")
    private Integer maxUsesPerCustomer;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        
        if (!active) {
            return false;
        }
        
        if (now.isBefore(validFrom) || now.isAfter(validUntil)) {
            return false;
        }
        
        if (maxUses != null && currentUses >= maxUses) {
            return false;
        }
        
        return true;
    }
    
    public boolean isApplicable(Money purchaseAmount) {
        if (minPurchase != null && purchaseAmount.getAmount().compareTo(minPurchase) < 0) {
            return false;
        }
        
        return true;
    }
    
    public Money calculateDiscount(Money amount) {
        if (!isValid() || !isApplicable(amount)) {
            return Money.zero();
        }
        
        Money discount;
        
        if (discountType == DiscountType.PERCENTAGE) {
            BigDecimal percentage = discountValue.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
            discount = amount.multiply(percentage);
        } else {
            discount = Money.of(discountValue);
        }
        
        // Apply max discount limit if set
        if (maxDiscount != null) {
            Money maxDiscountMoney = Money.of(maxDiscount);
            if (discount.isGreaterThan(maxDiscountMoney)) {
                discount = maxDiscountMoney;
            }
        }
        
        // Discount cannot exceed purchase amount
        if (discount.isGreaterThan(amount)) {
            discount = amount;
        }
        
        return discount;
    }
    
    public void registerUse() {
        this.currentUses++;
    }
    
    public boolean canBeUsedByCustomer(Integer customerUses) {
        if (maxUsesPerCustomer == null) {
            return true;
        }
        
        return customerUses < maxUsesPerCustomer;
    }
}