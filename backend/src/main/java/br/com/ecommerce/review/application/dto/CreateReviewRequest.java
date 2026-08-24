package br.com.ecommerce.review.application.dto;

import jakarta.validation.constraints.*;

public record CreateReviewRequest(
    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be between 1 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 1 and 5")
    java.math.BigDecimal rating,
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    String title,
    
    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 2000, message = "Content must be between 10 and 2000 characters")
    String content
) {}