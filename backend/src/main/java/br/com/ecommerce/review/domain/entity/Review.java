package br.com.ecommerce.review.domain.entity;

import br.com.ecommerce.auth.domain.entity.User;
import br.com.ecommerce.catalog.domain.entity.Product;
import br.com.ecommerce.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_reviews_product_id", columnList = "product_id"),
    @Index(name = "idx_reviews_user_id", columnList = "user_id"),
    @Index(name = "idx_reviews_rating", columnList = "rating")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends AuditableEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "helpful_count")
    @Builder.Default
    private Integer helpfulCount = 0;
    
    @Column(name = "unhelpful_count")
    @Builder.Default
    private Integer unhelpfulCount = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    public void incrementHelpful() {
        this.helpfulCount++;
    }
    
    public void incrementUnhelpful() {
        this.unhelpfulCount++;
    }
    
    public boolean isValidRating() {
        return rating.compareTo(BigDecimal.ONE) >= 0 && 
               rating.compareTo(BigDecimal.valueOf(5)) <= 0;
    }
}