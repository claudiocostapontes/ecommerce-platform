package br.com.ecommerce.customer.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAddressRequest(
    @NotBlank(message = "Label is required")
    @Size(max = 100, message = "Label must not exceed 100 characters")
    String label,
    
    @NotBlank(message = "Street is required")
    String street,
    
    @NotBlank(message = "Number is required")
    @Size(max = 10, message = "Number must not exceed 10 characters")
    String number,
    
    String complement,
    
    @NotBlank(message = "Neighborhood is required")
    String neighborhood,
    
    @NotBlank(message = "City is required")
    String city,
    
    @NotBlank(message = "State is required")
    String state,
    
    @NotBlank(message = "Zip code is required")
    String zipCode,
    
    Boolean isDefault
) {}