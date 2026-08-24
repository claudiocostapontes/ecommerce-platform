package br.com.ecommerce.shipping.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateShipmentRequest(
    @NotNull(message = "Order ID is required")
    UUID orderId,
    
    @NotBlank(message = "Tracking code is required")
    String trackingCode,
    
    @NotBlank(message = "Carrier is required")
    String carrier,
    
    @NotBlank(message = "Shipping method is required")
    String shippingMethod,
    
    BigDecimal cost,
    
    LocalDateTime estimatedDelivery,
    
    String notes
) {}