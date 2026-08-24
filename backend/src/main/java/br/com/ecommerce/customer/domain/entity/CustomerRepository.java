package br.com.ecommerce.customer.domain.repository;

import br.com.ecommerce.customer.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    
    Optional<Customer> findByUserId(UUID userId);
    
    boolean existsByUserId(UUID userId);
}