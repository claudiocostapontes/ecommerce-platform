package br.com.ecommerce.payment.application.dto;

import br.com.ecommerce.payment.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record ProcessPaymentRequest(
    @NotNull(message = "Order ID is required")
    UUID orderId,
    
    @NotNull(message = "Payment method is required")
    PaymentMethod method,
    
    Map<String, Object> paymentDetails,
    
    Integer installments,
    
    String idempotencyKey
) {}