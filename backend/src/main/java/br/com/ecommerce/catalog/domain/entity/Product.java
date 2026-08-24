package br.com.ecommerce.catalog.domain.entity;

import br.com.ecommerce.catalog.domain.valueobject.EAN;
import br.com.ecommerce.catalog.domain.valueobject.Money;
import br.com.ecommerce.catalog.domain.valueobject.SKU;
import br.com.ecommerce.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_products_slug", columnList = "slug"),
    @Index(name = "idx_products_sku", columnList = "sku"),
    @Index(name = "idx_products_category_id", columnList = "category_id"),
    @Index(name = "idx_products_brand_id", columnList = "brand_id"),
    @Index(name = "idx_products_active", columnList = "active"),
    @Index(name = "idx_products_featured", columnList = "featured")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends AuditableEntity {

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Embedded
    @AttributeOverride(name = "code", column = @Column(name = "sku", nullable = false, unique = true, length = 50))
    private SKU sku;

    @Embedded
    @AttributeOverride(name = "code", column = @Column(name = "ean", length = 13))
    private EAN ean;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price", nullable = false, precision = 10, scale = 2))
    private Money price;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "promotional_price", precision = 10, scale = 2))
    private Money promotionalPrice;

    @Column(name = "promotion_start")
    private LocalDateTime promotionStart;

    @Column(name = "promotion_end")
    private LocalDateTime promotionEnd;

    @Column(precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(precision = 10, scale = 2)
    private BigDecimal width;

    @Column(precision = 10, scale = 2)
    private BigDecimal height;

    @Column(precision = 10, scale = 2)
    private BigDecimal depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ElementCollection
    @CollectionTable(name = "product_specifications", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "spec_key", length = 100)
    @Column(name = "spec_value", columnDefinition = "TEXT")
    @Builder.Default
    private Map<String, String> specifications = new java.util.HashMap<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean featured = false;

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "rating_average", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal ratingAverage = BigDecimal.ZERO;

    @Column(name = "rating_count")
    @Builder.Default
    private Integer ratingCount = 0;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void addImage(ProductImage image) {
        this.images.add(image);
        image.setProduct(this);
    }

    public void updateRating(BigDecimal newRating) {
        BigDecimal currentTotal = this.ratingAverage.multiply(BigDecimal.valueOf(this.ratingCount));
        BigDecimal newTotal = currentTotal.add(newRating);
        this.ratingCount++;
        this.ratingAverage = newTotal.divide(BigDecimal.valueOf(this.ratingCount), 2, BigDecimal.ROUND_HALF_UP);
    }

    public Money getCurrentPrice() {
        return isPromotionActive() && promotionalPrice != null ? promotionalPrice : price;
    }

    public BigDecimal getDiscountPercentage() {
        if (!isPromotionActive() || promotionalPrice == null) {
            return BigDecimal.ZERO;
        }
        return price.getAmount().subtract(promotionalPrice.getAmount())
                .divide(price.getAmount(), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public boolean isPromotionActive() {
        if (promotionalPrice == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (promotionStart != null && now.isBefore(promotionStart)) {
            return false;
        }
        if (promotionEnd != null && now.isAfter(promotionEnd)) {
            return false;
        }
        return true;
    }
}
