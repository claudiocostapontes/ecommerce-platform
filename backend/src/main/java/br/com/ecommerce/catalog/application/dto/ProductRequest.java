package br.com.ecommerce.catalog.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ProductRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    String name,
    
    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    String sku,
    
    String ean,
    
    @NotBlank(message = "Description is required")
    String description,
    
    @Size(max = 500, message = "Short description must not exceed 500 characters")
    String shortDescription,
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    BigDecimal price,
    
    @DecimalMin(value = "0.01", message = "Promotional price must be greater than zero")
    BigDecimal promotionalPrice,
    
    LocalDateTime promotionStart,
    
    LocalDateTime promotionEnd,
    
    BigDecimal weight,
    BigDecimal width,
    BigDecimal height,
    BigDecimal depth,
    
    @NotNull(message = "Category is required")
    UUID categoryId,
    
    UUID brandId,
    
    List<ProductImageRequest> images,
    
    Map<String, Object> specifications,
    
    Boolean featured
) {}