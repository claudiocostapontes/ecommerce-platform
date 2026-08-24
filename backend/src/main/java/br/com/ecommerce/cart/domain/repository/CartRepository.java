package br.com.ecommerce.cart.domain.repository;

import br.com.ecommerce.cart.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.id = :id")
    Optional<Cart> findByIdWithItems(UUID id);
    
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithItems(UUID userId);
    
    Optional<Cart> findByUserId(UUID userId);
    
    Optional<Cart> findBySessionId(String sessionId);
    
    @Query("SELECT c FROM Cart c WHERE c.lastActivity < :threshold")
    List<Cart> findInactiveCarts(LocalDateTime threshold);
}