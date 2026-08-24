package br.com.ecommerce.customer.infrastructure.controller;

import br.com.ecommerce.customer.application.dto.CreateAddressRequest;
import br.com.ecommerce.customer.application.dto.CustomerAddressDTO;
import br.com.ecommerce.customer.application.dto.CustomerProfileDTO;
import br.com.ecommerce.customer.application.dto.UpdateCustomerRequest;
import br.com.ecommerce.customer.application.service.CustomerService;
import br.com.ecommerce.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Customer", description = "Customer profile and address management")
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @Operation(summary = "Get customer profile")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<CustomerProfileDTO>> getProfile(Authentication authentication) {
        CustomerProfileDTO profile = customerService.getProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
    
    @Operation(summary = "Update customer profile")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<CustomerProfileDTO>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateCustomerRequest request) {
        CustomerProfileDTO profile = customerService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }
    
    @Operation(summary = "Get customer addresses")
    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<CustomerAddressDTO>>> getAddresses(Authentication authentication) {
        List<CustomerAddressDTO> addresses = customerService.getAddresses(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }
    
    @Operation(summary = "Create address")
    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<CustomerAddressDTO>> createAddress(
            Authentication authentication,
            @Valid @RequestBody CreateAddressRequest request) {
        CustomerAddressDTO address = customerService.createAddress(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address created successfully", address));
    }
    
    @Operation(summary = "Update address")
    @PutMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<CustomerAddressDTO>> updateAddress(
            @PathVariable UUID id,
            @Valid @RequestBody CreateAddressRequest request) {
        CustomerAddressDTO address = customerService.updateAddress(id, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", address));
    }
    
    @Operation(summary = "Delete address")
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable UUID id) {
        customerService.deleteAddress(id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }
    
    @Operation(summary = "Add product to favorites")
    @PostMapping("/favorites/{productId}")
    public ResponseEntity<ApiResponse<Void>> addToFavorites(
            Authentication authentication,
            @PathVariable UUID productId) {
        customerService.addToFavorites(authentication.getName(), productId);
        return ResponseEntity.ok(ApiResponse.success("Product added to favorites", null));
    }
    
    @Operation(summary = "Remove product from favorites")
    @DeleteMapping("/favorites/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromFavorites(
            Authentication authentication,
            @PathVariable UUID productId) {
        customerService.removeFromFavorites(authentication.getName(), productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from favorites", null));
    }
}