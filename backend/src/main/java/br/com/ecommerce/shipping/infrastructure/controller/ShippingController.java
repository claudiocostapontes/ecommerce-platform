package br.com.ecommerce.shipping.infrastructure.controller;

import br.com.ecommerce.shipping.application.dto.CreateShipmentRequest;
import br.com.ecommerce.shipping.application.dto.ShipmentDTO;
import br.com.ecommerce.shipping.application.dto.ShippingQuoteDTO;
import br.com.ecommerce.shipping.application.dto.ShippingQuoteRequest;
import br.com.ecommerce.shipping.application.service.ShippingService;
import br.com.ecommerce.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Shipping", description = "Shipping and tracking endpoints")
@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {
    
    private final ShippingService shippingService;
    
    @Operation(summary = "Get shipping quotes")
    @GetMapping("/quotes")
    public ResponseEntity<ApiResponse<List<ShippingQuoteDTO>>> getShippingQuotes(
            @RequestParam String zipCode,
            @RequestParam Integer weight,
            @RequestParam Integer quantity) {
        List<ShippingQuoteDTO> quotes = shippingService.getShippingQuotes(
                new ShippingQuoteRequest(zipCode, weight, quantity));
        return ResponseEntity.ok(ApiResponse.success(quotes));
    }
    
    @Operation(summary = "Get shipment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentDTO>> getShipment(@PathVariable UUID id) {
        ShipmentDTO shipment = shippingService.getShipment(id);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }
    
    @Operation(summary = "Get shipment by tracking code")
    @GetMapping("/tracking/{trackingCode}")
    public ResponseEntity<ApiResponse<ShipmentDTO>> getShipmentByTracking(@PathVariable String trackingCode) {
        ShipmentDTO shipment = shippingService.getShipmentByTracking(trackingCode);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }
    
    @Operation(summary = "Get shipment by order", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ShipmentDTO>> getShipmentByOrder(@PathVariable UUID orderId) {
        ShipmentDTO shipment = shippingService.getShipmentByOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }
    
    @Operation(summary = "Create shipment", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ApiResponse<ShipmentDTO>> createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        ShipmentDTO shipment = shippingService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Shipment created successfully", shipment));
    }
    
    @Operation(summary = "Mark shipment as delivered", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/delivered")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ApiResponse<Void>> markAsDelivered(@PathVariable UUID id) {
        shippingService.markAsDelivered(id);
        return ResponseEntity.ok(ApiResponse.success("Shipment marked as delivered", null));
    }
}