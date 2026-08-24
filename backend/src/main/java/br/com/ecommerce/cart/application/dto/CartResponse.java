package br.com.ecommerce.cart.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartResponse {
    private UUID id;
    private List<CartItemResponse> items;
    private BigDecimal subtotal;
    private Integer totalItems;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CartItemResponse {
        private UUID id;
        private UUID productId;
        private String productName;
        private String productSku;
        private String productImage;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private Boolean available;
    }
}