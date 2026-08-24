package br.com.ecommerce.catalog.infrastructure.controller;

import br.com.ecommerce.catalog.application.dto.CategoryDTO;
import br.com.ecommerce.catalog.application.dto.CategoryRequest;
import br.com.ecommerce.catalog.application.service.CategoryService;
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

@Tag(name = "Categories", description = "Category management endpoints")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all root categories")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllRootCategories() {
        List<CategoryDTO> categories = categoryService.findAllRootCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @Operation(summary = "Get category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(@PathVariable UUID id) {
        CategoryDTO category = categoryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @Operation(summary = "Get category by slug")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryBySlug(@PathVariable String slug) {
        CategoryDTO category = categoryService.findBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @Operation(summary = "Create category", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryDTO category = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }

    @Operation(summary = "Update category", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryDTO category = categoryService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }

    @Operation(summary = "Delete category", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}