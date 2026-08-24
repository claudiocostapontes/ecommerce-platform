package br.com.ecommerce.inventory.domain.entity;

import br.com.ecommerce.catalog.domain.entity.Product;
import br.com.ecommerce.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory", indexes = {
    @Index(name = "idx_inventory_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends AuditableEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
    
    @Column(name = "quantity_available", nullable = false)
    @Builder.Default
    private Integer quantityAvailable = 0;
    
    @Column(name = "quantity_reserved", nullable = false)
    @Builder.Default
    private Integer quantityReserved = 0;
    
    @Column(name = "minimum_stock")
    @Builder.Default
    private Integer minimumStock = 0;
    
    @Column(name = "location", length = 100)
    private String location;
    
    public Integer getTotalQuantity() {
        return quantityAvailable + quantityReserved;
    }
    
    public boolean hasStock(Integer quantity) {
        return quantityAvailable >= quantity;
    }
    
    public void addStock(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantityAvailable += quantity;
    }
    
    public void removeStock(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantityAvailable < quantity) {
            throw new IllegalStateException("Insufficient stock available");
        }
        this.quantityAvailable -= quantity;
    }
    
    public void reserveStock(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantityAvailable < quantity) {
            throw new IllegalStateException("Insufficient stock to reserve");
        }
        this.quantityAvailable -= quantity;
        this.quantityReserved += quantity;
    }
    
    public void releaseReservation(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantityReserved < quantity) {
            throw new IllegalStateException("Insufficient reserved stock to release");
        }
        this.quantityReserved -= quantity;
        this.quantityAvailable += quantity;
    }
    
    public void confirmReservation(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantityReserved < quantity) {
            throw new IllegalStateException("Insufficient reserved stock to confirm");
        }
        this.quantityReserved -= quantity;
    }
    
    public boolean isBelowMinimum() {
        return getTotalQuantity() < minimumStock;
    }
}