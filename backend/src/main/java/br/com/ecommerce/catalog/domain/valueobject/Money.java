package br.com.ecommerce.catalog.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Money {

    private BigDecimal amount;

    public static Money of(BigDecimal amount) {
        return new Money(amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    public static Money of(double amount) {
        return of(BigDecimal.valueOf(amount));
    }

    public static Money zero() {
        return of(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        return of(this.amount.add(other != null ? other.amount : BigDecimal.ZERO));
    }

    public Money subtract(Money other) {
        return of(this.amount.subtract(other != null ? other.amount : BigDecimal.ZERO));
    }

    public Money multiply(BigDecimal multiplier) {
        return of(this.amount.multiply(multiplier != null ? multiplier : BigDecimal.ONE));
    }

    public Money multiply(double multiplier) {
        return multiply(BigDecimal.valueOf(multiplier));
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other != null ? other.amount : BigDecimal.ZERO) > 0;
    }

    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other != null ? other.amount : BigDecimal.ZERO) < 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }
}
