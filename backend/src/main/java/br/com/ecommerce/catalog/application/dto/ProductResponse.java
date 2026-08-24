package br.com.ecommerce.catalog.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private UUID id;
    private String name;
    private String slug;
    private String sku;
    private String ean;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal promotionalPrice;
    private BigDecimal discountPercentage;
    private Boolean onPromotion;
    private String categoryName;
    private UUID categoryId;
    private String brandName;
    private UUID brandId;
    private String primaryImage;
    private Boolean active;
    private Boolean featured;
    private BigDecimal ratingAverage;
    private Integer ratingCount;
}