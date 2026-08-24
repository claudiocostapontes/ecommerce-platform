package br.com.ecommerce.payment.domain.service;

import br.com.ecommerce.payment.application.dto.ProcessPaymentRequest;

public interface PaymentGateway {
    
    PaymentGatewayResponse processPayment(ProcessPaymentRequest request);
    
    PaymentGatewayResponse refundPayment(String gatewayTransactionId, java.math.BigDecimal amount);
    
    PaymentGatewayResponse queryPaymentStatus(String gatewayTransactionId);
    
    boolean supportsMethod(br.com.ecommerce.payment.domain.enums.PaymentMethod method);
    
    String getGatewayName();
    
    record PaymentGatewayResponse(
        boolean success,
        String transactionId,
        String status,
        String message,
        java.util.Map<String, Object> metadata
    ) {}
}