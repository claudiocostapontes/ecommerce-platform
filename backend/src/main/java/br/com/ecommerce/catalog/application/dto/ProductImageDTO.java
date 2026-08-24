package br.com.ecommerce.catalog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
    private UUID id;
    private String imageUrl;
    private String thumbnailUrl;
    private String altText;
    private Integer displayOrder;
    private Boolean isPrimary;
}