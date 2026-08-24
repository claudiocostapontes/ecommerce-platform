package br.com.ecommerce.cart.domain.entity;

import br.com.ecommerce.catalog.domain.entity.Product;
import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items", indexes = {
    @Index(name = "idx_cart_items_cart_id", columnList = "cart_id"),
    @Index(name = "idx_cart_items_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "unit_price", nullable = false, precision = 10, scale = 2))
    private Money unitPrice;
    
    public Money getSubtotal() {
        return unitPrice.multiply(quantity);
    }
    
    public void incrementQuantity(int amount) {
        this.quantity += amount;
    }
    
    public void decrementQuantity(int amount) {
        if (this.quantity - amount < 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }
        this.quantity -= amount;
    }
}