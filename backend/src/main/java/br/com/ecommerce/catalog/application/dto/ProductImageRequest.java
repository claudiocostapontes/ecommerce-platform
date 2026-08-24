package br.com.ecommerce.catalog.application.dto;

public record ProductImageRequest(
        String imageUrl,
        String thumbnailUrl,
        String altText,
        Integer displayOrder,
        Boolean isPrimary
) {}