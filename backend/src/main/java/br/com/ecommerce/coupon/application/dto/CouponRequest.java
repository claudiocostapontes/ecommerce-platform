package br.com.ecommerce.coupon.application.dto;

import br.com.ecommerce.coupon.domain.enums.DiscountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponRequest(
    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    String code,
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,
    
    @NotNull(message = "Discount type is required")
    DiscountType discountType,
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than zero")
    BigDecimal discountValue,
    
    @DecimalMin(value = "0", message = "Max discount must be positive")
    BigDecimal maxDiscount,
    
    @DecimalMin(value = "0", message = "Min purchase must be positive")
    BigDecimal minPurchase,
    
    @NotNull(message = "Valid from date is required")
    LocalDateTime validFrom,
    
    @NotNull(message = "Valid until date is required")
    LocalDateTime validUntil,
    
    @Min(value = 1, message = "Max uses must be at least 1")
    Integer maxUses,
    
    @Min(value = 1, message = "Max uses per customer must be at least 1")
    Integer maxUsesPerCustomer
) {}