package br.com.ecommerce.payment.infrastructure.gateway;

import br.com.ecommerce.payment.application.dto.ProcessPaymentRequest;
import br.com.ecommerce.payment.domain.enums.PaymentMethod;
import br.com.ecommerce.payment.domain.service.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class MockPaymentGateway implements PaymentGateway {
    
    @Override
    public PaymentGatewayResponse processPayment(ProcessPaymentRequest request) {
        log.info("Processing payment with Mock Gateway for order: {}", request.orderId());
        
        // Simulate processing
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String transactionId = "MOCK-" + UUID.randomUUID();
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("gateway", "MOCK");
        metadata.put("method", request.method().name());
        metadata.put("timestamp", System.currentTimeMillis());
        
        // Mock: 90% success rate
        boolean success = Math.random() > 0.1;
        
        if (success) {
            log.info("Mock payment authorized: {}", transactionId);
            return new PaymentGatewayResponse(true, transactionId, "AUTHORIZED", "Payment authorized", metadata);
        } else {
            return new PaymentGatewayResponse(false, null, "DECLINED", "Card declined", metadata);
        }
    }
    
    @Override
    public PaymentGatewayResponse refundPayment(String gatewayTransactionId, BigDecimal amount) {
        log.info("Processing refund with Mock Gateway: {} amount: {}", gatewayTransactionId, amount);
        
        String refundId = "REFUND-" + UUID.randomUUID();
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("refund_id", refundId);
        metadata.put("original_transaction", gatewayTransactionId);
        
        return new PaymentGatewayResponse(true, refundId, "REFUNDED", "Refund processed", metadata);
    }
    
    @Override
    public PaymentGatewayResponse queryPaymentStatus(String gatewayTransactionId) {
        log.info("Querying payment status: {}", gatewayTransactionId);
        
        return new PaymentGatewayResponse(true, gatewayTransactionId, "PAID", "Payment confirmed", new HashMap<>());
    }
    
    @Override
    public boolean supportsMethod(PaymentMethod method) {
        return true; // Mock gateway supports all methods
    }
    
    @Override
    public String getGatewayName() {
        return "MOCK";
    }
}