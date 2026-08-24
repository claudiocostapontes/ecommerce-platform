package br.com.ecommerce.inventory.infrastructure.controller;

import br.com.ecommerce.inventory.application.dto.InventoryDTO;
import br.com.ecommerce.inventory.application.dto.InventoryMovementDTO;
import br.com.ecommerce.inventory.application.dto.StockAdjustmentRequest;
import br.com.ecommerce.inventory.application.service.InventoryService;
import br.com.ecommerce.shared.dto.ApiResponse;
import br.com.ecommerce.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Inventory", description = "Inventory management endpoints")
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @Operation(summary = "Get inventory by product ID")
    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<ApiResponse<InventoryDTO>> getInventoryByProduct(@PathVariable UUID productId) {
        InventoryDTO inventory = inventoryService.findByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }
    
    @Operation(summary = "Get products below minimum stock")
    @GetMapping("/below-minimum")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getBelowMinimum() {
        List<InventoryDTO> inventory = inventoryService.findBelowMinimum();
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }
    
    @Operation(summary = "Get out of stock products")
    @GetMapping("/out-of-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getOutOfStock() {
        List<InventoryDTO> inventory = inventoryService.findOutOfStock();
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }
    
    @Operation(summary = "Get movement history")
    @GetMapping("/movements/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<ApiResponse<PageResponse<InventoryMovementDTO>>> getMovementHistory(
            @PathVariable UUID productId,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<InventoryMovementDTO> movements = inventoryService.getMovementHistory(productId, pageable);
        return ResponseEntity.ok(ApiResponse.success(movements));
    }
    
    @Operation(summary = "Add stock")
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<ApiResponse<InventoryDTO>> addStock(@Valid @RequestBody StockAdjustmentRequest request) {
        InventoryDTO inventory = inventoryService.addStock(request);
        return ResponseEntity.ok(ApiResponse.success("Stock added successfully", inventory));
    }
    
    @Operation(summary = "Adjust stock")
    @PostMapping("/adjust/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> adjustStock(
            @PathVariable UUID productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String notes) {
        InventoryDTO inventory = inventoryService.adjustStock(productId, quantity, notes);
        return ResponseEntity.ok(ApiResponse.success("Stock adjusted successfully", inventory));
    }
    
    @Operation(summary = "Set minimum stock")
    @PatchMapping("/minimum/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> setMinimumStock(
            @PathVariable UUID productId,
            @RequestParam Integer minimumStock) {
        InventoryDTO inventory = inventoryService.setMinimumStock(productId, minimumStock);
        return ResponseEntity.ok(ApiResponse.success("Minimum stock set successfully", inventory));
    }
}