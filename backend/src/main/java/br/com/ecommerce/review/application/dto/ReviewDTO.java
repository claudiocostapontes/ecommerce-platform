package br.com.ecommerce.review.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDTO {
    private UUID id;
    private UUID productId;
    private UUID userId;
    private String userName;
    private BigDecimal rating;
    private String title;
    private String content;
    private Integer helpfulCount;
    private Integer unhelpfulCount;
    private Boolean verified;
    private LocalDateTime createdAt;
}