package br.com.ecommerce.order.domain.entity;

import br.com.ecommerce.catalog.domain.entity.Product;
import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_items_order_id", columnList = "order_id"),
    @Index(name = "idx_order_items_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "unit_price", nullable = false, precision = 10, scale = 2))
    private Money unitPrice;
    
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;
    
    @Column(name = "product_sku", nullable = false, length = 50)
    private String productSku;
    
    public Money getSubtotal() {
        return unitPrice.multiply(quantity);
    }
}
