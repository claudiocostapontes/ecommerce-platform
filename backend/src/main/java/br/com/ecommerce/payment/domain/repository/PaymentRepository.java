package br.com.ecommerce.payment.domain.repository;

import br.com.ecommerce.payment.domain.entity.Payment;
import br.com.ecommerce.payment.domain.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    Optional<Payment> findByOrderId(UUID orderId);
    
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
    
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
    
    List<Payment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    Long countByStatusAndCreatedAtAfter(PaymentStatus status, LocalDateTime date);
}