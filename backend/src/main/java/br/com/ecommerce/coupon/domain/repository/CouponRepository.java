package br.com.ecommerce.coupon.domain.repository;

import br.com.ecommerce.coupon.domain.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    
    Optional<Coupon> findByCode(String code);
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true AND c.validFrom <= CURRENT_TIMESTAMP AND c.validUntil >= CURRENT_TIMESTAMP")
    List<Coupon> findActiveCoupons();
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true AND c.validFrom <= CURRENT_TIMESTAMP AND c.validUntil >= CURRENT_TIMESTAMP")
    Page<Coupon> findActiveCoupons(Pageable pageable);
    
    @Query("SELECT c FROM Coupon c WHERE c.validUntil < :date AND c.active = true")
    List<Coupon> findExpiredCoupons(LocalDateTime date);
    
    Page<Coupon> findByActiveTrue(Pageable pageable);
    
    boolean existsByCode(String code);
}