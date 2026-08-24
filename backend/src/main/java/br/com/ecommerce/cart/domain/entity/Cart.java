package br.com.ecommerce.cart.domain.entity;

import br.com.ecommerce.auth.domain.entity.User;
import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts", indexes = {
    @Index(name = "idx_carts_user_id", columnList = "user_id"),
    @Index(name = "idx_carts_session_id", columnList = "session_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart extends AuditableEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        updateLastActivity();
    }
    
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        updateLastActivity();
    }
    
    public void clear() {
        items.clear();
        updateLastActivity();
    }
    
    public Money getSubtotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(Money.zero(), Money::add);
    }
    
    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
}