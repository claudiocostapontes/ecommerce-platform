package br.com.ecommerce.cart.infrastructure.controller;

import br.com.ecommerce.cart.application.dto.AddToCartRequest;
import br.com.ecommerce.cart.application.dto.CartResponse;
import br.com.ecommerce.cart.application.dto.UpdateCartItemRequest;
import br.com.ecommerce.cart.application.service.CartService;
import br.com.ecommerce.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Cart", description = "Shopping cart endpoints")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CartController {
    
    private final CartService cartService;
    
    @Operation(summary = "Get cart")
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication authentication) {
        CartResponse cart = cartService.getCart(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(cart));
    }
    
    @Operation(summary = "Add item to cart")
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addItem(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", cart));
    }
    
    @Operation(summary = "Update cart item")
    @PutMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            Authentication authentication,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateItem(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Item updated", cart));
    }
    
    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            Authentication authentication,
            @PathVariable UUID itemId) {
        CartResponse cart = cartService.removeItem(authentication.getName(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }
    
    @Operation(summary = "Clear cart")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
}