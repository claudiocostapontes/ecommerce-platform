package br.com.ecommerce.coupon.infrastructure.controller;

import br.com.ecommerce.coupon.application.dto.CouponDTO;
import br.com.ecommerce.coupon.application.dto.CouponRequest;
import br.com.ecommerce.coupon.application.service.CouponService;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Coupons", description = "Coupon management endpoints")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {
    
    private final CouponService couponService;
    
    @Operation(summary = "Validate coupon", description = "Validate coupon code and purchase amount")
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<CouponDTO>> validateCoupon(
            @RequestParam String code,
            @RequestParam java.math.BigDecimal purchaseAmount) {
        var money = br.com.ecommerce.catalog.domain.valueobject.Money.of(purchaseAmount);
        CouponDTO coupon = couponService.validateCoupon(code, money);
        return ResponseEntity.ok(ApiResponse.success(coupon));
    }
    
    @Operation(summary = "Get coupon by code")
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<CouponDTO>> getCoupon(@PathVariable String code) {
        CouponDTO coupon = couponService.findByCode(code);
        return ResponseEntity.ok(ApiResponse.success(coupon));
    }
    
    @Operation(summary = "Get active coupons")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CouponDTO>>> getActiveCoupons(
            @PageableDefault(size = 20, sort = "validUntil", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<CouponDTO> coupons = couponService.findActiveCoupons(pageable);
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }
    
    @Operation(summary = "Get all coupons", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<CouponDTO>>> getAllCoupons(
            @PageableDefault(size = 20, sort = "validUntil", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<CouponDTO> coupons = couponService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }
    
    @Operation(summary = "Create coupon", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CouponDTO>> createCoupon(@Valid @RequestBody CouponRequest request) {
        CouponDTO coupon = couponService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Coupon created successfully", coupon));
    }
    
    @Operation(summary = "Update coupon", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CouponDTO>> updateCoupon(
            @PathVariable UUID id,
            @Valid @RequestBody CouponRequest request) {
        CouponDTO coupon = couponService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Coupon updated successfully", coupon));
    }
    
    @Operation(summary = "Deactivate coupon", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateCoupon(@PathVariable UUID id) {
        couponService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deactivated", null));
    }
}