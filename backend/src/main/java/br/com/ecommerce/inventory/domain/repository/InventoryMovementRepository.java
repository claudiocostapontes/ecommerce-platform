package br.com.ecommerce.inventory.domain.repository;

import br.com.ecommerce.inventory.domain.entity.InventoryMovement;
import br.com.ecommerce.inventory.domain.enums.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    
    Page<InventoryMovement> findByProductIdOrderByCreatedAtDesc(UUID productId, Pageable pageable);
    
    List<InventoryMovement> findByReferenceId(UUID referenceId);
    
    @Query("SELECT im FROM InventoryMovement im WHERE im.product.id = :productId AND im.movementType = :type ORDER BY im.createdAt DESC")
    Page<InventoryMovement> findByProductIdAndType(UUID productId, MovementType type, Pageable pageable);
}