package br.com.ecommerce.order.application.dto;

import br.com.ecommerce.order.domain.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private UUID id;
    private String orderNumber;
    private OrderStatus status;
    private List<OrderItemDTO> items;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal shippingCost;
    private BigDecimal total;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryZipCode;
    private String trackingCode;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private UUID id;
        private UUID productId;
        private String productName;
        private String productSku;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}