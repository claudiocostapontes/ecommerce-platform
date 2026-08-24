package br.com.ecommerce.inventory.domain.repository;

import br.com.ecommerce.inventory.domain.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId")
    Optional<Inventory> findByProductIdWithLock(UUID productId);
    
    Optional<Inventory> findByProductId(UUID productId);
    
    @Query("SELECT i FROM Inventory i WHERE i.quantityAvailable + i.quantityReserved < i.minimumStock")
    List<Inventory> findBelowMinimumStock();
    
    @Query("SELECT i FROM Inventory i WHERE i.quantityAvailable = 0 AND i.quantityReserved = 0")
    List<Inventory> findOutOfStock();
}