package br.com.ecommerce.order.infrastructure.controller;

import br.com.ecommerce.order.application.dto.CreateOrderRequest;
import br.com.ecommerce.order.application.dto.OrderResponse;
import br.com.ecommerce.order.application.dto.UpdateOrderStatusRequest;
import br.com.ecommerce.order.application.service.OrderService;
import br.com.ecommerce.order.domain.enums.OrderStatus;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Orders", description = "Order management endpoints")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    
    private final OrderService orderService;
    
    @Operation(summary = "Create order")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.createOrder(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", order));
    }
    
    @Operation(summary = "Get order by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable UUID id) {
        OrderResponse order = orderService.getOrder(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    @Operation(summary = "Get order by order number")
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(@PathVariable String orderNumber) {
        OrderResponse order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    @Operation(summary = "Get user orders")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getUserOrders(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<OrderResponse> orders = orderService.getUserOrders(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    @Operation(summary = "Get orders by status", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<OrderResponse> orders = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    @Operation(summary = "Update order status", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse order = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
    }
    
    @Operation(summary = "Add admin notes", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/notes")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> addAdminNotes(
            @PathVariable UUID id,
            @RequestParam String notes) {
        orderService.addAdminNotes(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Notes added", null));
    }
    
    @Operation(summary = "Set tracking code", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/tracking")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ApiResponse<Void>> setTrackingCode(
            @PathVariable UUID id,
            @RequestParam String trackingCode) {
        orderService.setTrackingCode(id, trackingCode);
        return ResponseEntity.ok(ApiResponse.success("Tracking code set", null));
    }
}