package br.com.ecommerce.shipping.application.dto;

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
public class ShipmentDTO {
    private UUID id;
    private UUID orderId;
    private String trackingCode;
    private String carrier;
    private String shippingMethod;
    private BigDecimal cost;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime actualDelivery;
    private String recipientName;
    private String recipientAddress;
    private String notes;
}