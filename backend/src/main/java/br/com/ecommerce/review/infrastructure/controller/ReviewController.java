package br.com.ecommerce.review.infrastructure.controller;

import br.com.ecommerce.review.application.dto.CreateReviewRequest;
import br.com.ecommerce.review.application.dto.ReviewDTO;
import br.com.ecommerce.review.application.service.ReviewService;
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

@Tag(name = "Reviews", description = "Product review endpoints")
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @Operation(summary = "Get product reviews")
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> getProductReviews(
            @PathVariable UUID productId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ReviewDTO> reviews = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @Operation(summary = "Get user reviews", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> getUserReviews(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ReviewDTO> reviews = reviewService.getUserReviews(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @Operation(summary = "Get pending approvals", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> getPendingApproval(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<ReviewDTO> reviews = reviewService.getPendingApproval(pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @Operation(summary = "Create review", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(
            @PathVariable UUID productId,
            Authentication authentication,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewDTO review = reviewService.createReview(productId, authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review created successfully (pending approval)", review));
    }
    
    @Operation(summary = "Approve review", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> approveReview(@PathVariable UUID id) {
        reviewService.approveReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review approved", null));
    }
    
    @Operation(summary = "Reject review", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> rejectReview(@PathVariable UUID id) {
        reviewService.rejectReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review rejected", null));
    }
    
    @Operation(summary = "Delete review", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable UUID id,
            Authentication authentication) {
        reviewService.deleteReview(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Review deleted", null));
    }
    
    @Operation(summary = "Mark as helpful")
    @PatchMapping("/{id}/helpful")
    public ResponseEntity<ApiResponse<Void>> markAsHelpful(@PathVariable UUID id) {
        reviewService.markAsHelpful(id);
        return ResponseEntity.ok(ApiResponse.success("Marked as helpful", null));
    }
    
    @Operation(summary = "Mark as unhelpful")
    @PatchMapping("/{id}/unhelpful")
    public ResponseEntity<ApiResponse<Void>> markAsUnhelpful(@PathVariable UUID id) {
        reviewService.markAsUnhelpful(id);
        return ResponseEntity.ok(ApiResponse.success("Marked as unhelpful", null));
    }
}