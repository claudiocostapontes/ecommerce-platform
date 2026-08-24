package br.com.ecommerce.order.domain.repository;

import br.com.ecommerce.order.domain.entity.OrderStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {
    
    Page<OrderStatusHistory> findByOrderId(UUID orderId, Pageable pageable);
}