package br.com.ecommerce.order.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrderRequest(
    @NotNull(message = "Cart ID is required")
    UUID cartId,
    
    @NotBlank(message = "Delivery address is required")
    String deliveryAddress,
    
    @NotBlank(message = "Delivery city is required")
    String deliveryCity,
    
    @NotBlank(message = "Delivery state is required")
    String deliveryState,
    
    @NotBlank(message = "Delivery zip code is required")
    String deliveryZipCode,
    
    @NotBlank(message = "Recipient name is required")
    String deliveryRecipientName,
    
    @NotBlank(message = "Recipient phone is required")
    String deliveryPhone,
    
    String customerNotes,
    
    String couponCode
) {}