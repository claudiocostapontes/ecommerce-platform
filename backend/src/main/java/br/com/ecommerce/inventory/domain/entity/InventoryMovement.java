package br.com.ecommerce.inventory.domain.entity;

import br.com.ecommerce.catalog.domain.entity.Product;
import br.com.ecommerce.inventory.domain.enums.MovementType;
import br.com.ecommerce.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "inventory_movements", indexes = {
    @Index(name = "idx_inventory_movements_product_id", columnList = "product_id"),
    @Index(name = "idx_inventory_movements_type", columnList = "movement_type"),
    @Index(name = "idx_inventory_movements_reference", columnList = "reference_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovement extends AuditableEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 50)
    private MovementType movementType;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "quantity_before", nullable = false)
    private Integer quantityBefore;
    
    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter;
    
    @Column(name = "reference_id")
    private UUID referenceId;
    
    @Column(name = "reference_type", length = 50)
    private String referenceType;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
}