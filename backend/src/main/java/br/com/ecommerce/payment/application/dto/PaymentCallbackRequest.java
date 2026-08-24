package br.com.ecommerce.payment.application.dto;

public record PaymentCallbackRequest(
    String transactionId,
    String status,
    String orderId,
    String errorMessage,
    String metadata
) {}