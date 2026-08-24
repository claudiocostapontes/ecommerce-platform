package br.com.ecommerce.customer.domain.entity;

import br.com.ecommerce.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_addresses", indexes = {
    @Index(name = "idx_customer_addresses_customer_id", columnList = "customer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAddress extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(nullable = false, length = 100)
    private String label;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String street;
    
    @Column(nullable = false, length = 10)
    private String number;
    
    @Column(length = 100)
    private String complement;
    
    @Column(nullable = false, length = 100)
    private String neighborhood;
    
    @Column(nullable = false, length = 100)
    private String city;
    
    @Column(nullable = false, length = 50)
    private String state;
    
    @Column(nullable = false, length = 20)
    private String zipCode;
    
    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;
    
    public String getFormattedAddress() {
        return String.format("%s, %s %s - %s, %s %s - %s",
                street, number, complement != null ? complement : "",
                neighborhood, city, state, zipCode);
    }
}