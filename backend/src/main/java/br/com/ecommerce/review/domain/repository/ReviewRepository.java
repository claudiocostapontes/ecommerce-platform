package br.com.ecommerce.review.domain.repository;

import br.com.ecommerce.review.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.active = true ORDER BY r.helpfulCount DESC, r.createdAt DESC")
    Page<Review> findByProductId(UUID productId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.user.id = :userId")
    Optional<Review> findByProductIdAndUserId(UUID productId, UUID userId);
    
    Page<Review> findByUserId(UUID userId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.active = false")
    Page<Review> findPendingApproval(Pageable pageable);
    
    Long countByProductId(UUID productId);
}