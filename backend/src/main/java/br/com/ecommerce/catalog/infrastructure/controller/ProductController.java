package br.com.ecommerce.catalog.infrastructure.controller;

import br.com.ecommerce.catalog.application.dto.ProductDetailResponse;
import br.com.ecommerce.catalog.application.dto.ProductRequest;
import br.com.ecommerce.catalog.application.dto.ProductResponse;
import br.com.ecommerce.catalog.application.service.ProductService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Products", description = "Product catalog endpoints")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all products")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ProductResponse> products = productService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(@PathVariable UUID id) {
        ProductDetailResponse product = productService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @Operation(summary = "Get product by slug")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductBySlug(@PathVariable String slug) {
        ProductDetailResponse product = productService.findBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @Operation(summary = "Get products by category")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByCategory(
            @PathVariable UUID categoryId,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        PageResponse<ProductResponse> products = productService.findByCategory(categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "Get products by brand")
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductsByBrand(
            @PathVariable UUID brandId,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        PageResponse<ProductResponse> products = productService.findByBrand(brandId, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "Search products")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<ProductResponse> products = productService.search(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "Get products with filters")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> filterProducts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<ProductResponse> products = productService.findWithFilters(
                categoryId, brandId, minPrice, maxPrice, search, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "Get featured products")
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getFeaturedProducts(
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<ProductResponse> products = productService.findFeatured(pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "Get most viewed products")
    @GetMapping("/most-viewed")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getMostViewedProducts() {
        List<ProductResponse> products = productService.findMostViewed();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "Get top rated products")
    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getTopRatedProducts() {
        List<ProductResponse> products = productService.findTopRated();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "Create product", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        ProductDetailResponse product = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", product));
    }

    @Operation(summary = "Update product", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        ProductDetailResponse product = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }

    @Operation(summary = "Delete product", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

    @Operation(summary = "Activate product", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> activateProduct(@PathVariable UUID id) {
        productService.activate(id);
        return ResponseEntity.ok(ApiResponse.success("Product activated successfully", null));
    }
}