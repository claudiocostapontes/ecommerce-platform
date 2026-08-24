package br.com.ecommerce.catalog.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDetailResponse {
    private UUID id;
    private String name;
    private String slug;
    private String sku;
    private String ean;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal promotionalPrice;
    private BigDecimal discountPercentage;
    private Boolean onPromotion;
    private LocalDateTime promotionStart;
    private LocalDateTime promotionEnd;
    private BigDecimal weight;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;
    private CategoryDTO category;
    private BrandDTO brand;
    private List<ProductImageDTO> images;
    private Map<String, String> specifications;
    private Boolean active;
    private Boolean featured;
    private Long viewCount;
    private BigDecimal ratingAverage;
    private Integer ratingCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}