package br.com.ecommerce.payment.application.dto;

import br.com.ecommerce.payment.domain.enums.PaymentMethod;
import br.com.ecommerce.payment.domain.enums.PaymentStatus;
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
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private PaymentMethod method;
    private PaymentStatus status;
    private BigDecimal amount;
    private BigDecimal refundedAmount;
    private String gatewayTransactionId;
    private String paymentReference;
    private LocalDateTime paidAt;
    private LocalDateTime failedAt;
    private String failureReason;
    private LocalDateTime createdAt;
}