package br.com.ecommerce.customer.application.dto;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateCustomerRequest(
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        String cpf,

        @PastOrPresent(message = "Birth date cannot be in the future")
        LocalDate birthDate,

        @Size(max = 20, message = "Phone must not exceed 20 characters")
        String phone,

        Boolean newsletterSubscribed,

        Boolean marketingNotifications
) {}