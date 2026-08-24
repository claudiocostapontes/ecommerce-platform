package br.com.ecommerce.catalog.infrastructure.controller;

import br.com.ecommerce.catalog.application.dto.BrandDTO;
import br.com.ecommerce.catalog.application.dto.BrandRequest;
import br.com.ecommerce.catalog.application.service.BrandService;
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

import java.util.List;
import java.util.UUID;

@Tag(name = "Brands", description = "Brand management endpoints")
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "Get all brands (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BrandDTO>>> getAllBrands(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<BrandDTO> brands = brandService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(brands));
    }

    @Operation(summary = "Get all active brands (list)")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BrandDTO>>> getAllActiveBrands() {
        List<BrandDTO> brands = brandService.findAllActive();
        return ResponseEntity.ok(ApiResponse.success(brands));
    }

    @Operation(summary = "Get brand by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandDTO>> getBrandById(@PathVariable UUID id) {
        BrandDTO brand = brandService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(brand));
    }

    @Operation(summary = "Get brand by slug")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<BrandDTO>> getBrandBySlug(@PathVariable String slug) {
        BrandDTO brand = brandService.findBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(brand));
    }

    @Operation(summary = "Create brand", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<BrandDTO>> createBrand(@Valid @RequestBody BrandRequest request) {
        BrandDTO brand = brandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Brand created successfully", brand));
    }

    @Operation(summary = "Update brand", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<BrandDTO>> updateBrand(
            @PathVariable UUID id,
            @Valid @RequestBody BrandRequest request) {
        BrandDTO brand = brandService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Brand updated successfully", brand));
    }

    @Operation(summary = "Delete brand", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable UUID id) {
        brandService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Brand deleted successfully", null));
    }
}