package br.com.ecommerce.customer.domain.entity;

import br.com.ecommerce.auth.domain.entity.User;
import br.com.ecommerce.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends AuditableEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "cpf", length = 20)
    private String cpf;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CustomerAddress> addresses = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "customer_favorites", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "product_id")
    @Builder.Default
    private Set<java.util.UUID> favoriteProductIds = new HashSet<>();
    
    @Column(name = "newsletter_subscribed")
    @Builder.Default
    private Boolean newsletterSubscribed = false;
    
    @Column(name = "marketing_notifications")
    @Builder.Default
    private Boolean marketingNotifications = false;
    
    public void addAddress(CustomerAddress address) {
        addresses.add(address);
        address.setCustomer(this);
    }
    
    public void removeAddress(CustomerAddress address) {
        addresses.remove(address);
        address.setCustomer(null);
    }
    
    public void setDefaultAddress(java.util.UUID addressId) {
        addresses.forEach(addr -> addr.setIsDefault(addr.getId().equals(addressId)));
    }
    
    public CustomerAddress getDefaultAddress() {
        return addresses.stream()
                .filter(CustomerAddress::getIsDefault)
                .findFirst()
                .orElse(null);
    }
    
    public void addFavorite(java.util.UUID productId) {
        favoriteProductIds.add(productId);
    }
    
    public void removeFavorite(java.util.UUID productId) {
        favoriteProductIds.remove(productId);
    }
    
    public boolean isFavorite(java.util.UUID productId) {
        return favoriteProductIds.contains(productId);
    }
}