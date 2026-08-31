package br.com.ecommerce.payment.infrastructure.controller;

import br.com.ecommerce.payment.application.dto.PaymentResponse;
import br.com.ecommerce.payment.application.dto.ProcessPaymentRequest;
import br.com.ecommerce.payment.application.dto.RefundPaymentRequest;
import br.com.ecommerce.payment.application.service.PaymentService;
import br.com.ecommerce.payment.domain.enums.PaymentStatus;
import br.com.ecommerce.shared.dto.ApiResponse;
import br.com.ecommerce.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
        name = "Payments",
        description = "Payment management endpoints"
)
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Process payment")
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            Authentication authentication,
            @Valid @RequestBody ProcessPaymentRequest request
    ) {
        PaymentResponse payment =
                paymentService.processPayment(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Payment processed",
                        payment
                )
        );
    }

    @Operation(summary = "Get payment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        PaymentResponse payment =
                paymentService.getPayment(
                        authentication.getName(),
                        id
                );

        return ResponseEntity.ok(
                ApiResponse.success(payment)
        );
    }

    @Operation(summary = "Get payment by order")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrder(
            Authentication authentication,
            @PathVariable UUID orderId
    ) {
        PaymentResponse payment =
                paymentService.getPaymentByOrder(
                        authentication.getName(),
                        orderId
                );

        return ResponseEntity.ok(
                ApiResponse.success(payment)
        );
    }

    @Operation(summary = "Get payments by status")
    @GetMapping("/status/{status}")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        PageResponse<PaymentResponse> payments =
                paymentService.getPaymentsByStatus(
                        status,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(payments)
        );
    }

    @Operation(summary = "Refund payment")
    @PostMapping("/{id}/refund")
    @PreAuthorize(
            "hasAnyRole('ADMIN', 'MANAGER')"
    )
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @PathVariable UUID id,
            @Valid @RequestBody RefundPaymentRequest request
    ) {
        PaymentResponse payment =
                paymentService.refundPayment(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Refund processed",
                        payment
                )
        );
    }
}