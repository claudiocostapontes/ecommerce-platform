package br.com.ecommerce.customer.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerProfileDTO {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String cpf;
    private LocalDate birthDate;
    private String phone;
    private List<CustomerAddressDTO> addresses;
    private Boolean newsletterSubscribed;
    private Boolean marketingNotifications;
}