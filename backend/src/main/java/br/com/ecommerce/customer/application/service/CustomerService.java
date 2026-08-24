package br.com.ecommerce.customer.application.service;

import br.com.ecommerce.auth.domain.entity.User;
import br.com.ecommerce.auth.domain.repository.UserRepository;
import br.com.ecommerce.customer.application.dto.CreateAddressRequest;
import br.com.ecommerce.customer.application.dto.CustomerAddressDTO;
import br.com.ecommerce.customer.application.dto.CustomerProfileDTO;
import br.com.ecommerce.customer.application.dto.UpdateCustomerRequest;
import br.com.ecommerce.customer.domain.entity.Customer;
import br.com.ecommerce.customer.domain.entity.CustomerAddress;
import br.com.ecommerce.customer.domain.repository.CustomerAddressRepository;
import br.com.ecommerce.customer.domain.repository.CustomerRepository;
import br.com.ecommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository addressRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public CustomerProfileDTO getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseGet(() -> createDefaultCustomer(user));
        
        return toProfileDTO(customer, user);
    }
    
    @Transactional
    public CustomerProfileDTO updateProfile(String username, UpdateCustomerRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseGet(() -> createDefaultCustomer(user));
        
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        
        if (request.cpf() != null) {
            customer.setCpf(request.cpf());
        }
        if (request.birthDate() != null) {
            customer.setBirthDate(request.birthDate());
        }
        if (request.phone() != null) {
            customer.setPhone(request.phone());
        }
        if (request.newsletterSubscribed() != null) {
            customer.setNewsletterSubscribed(request.newsletterSubscribed());
        }
        if (request.marketingNotifications() != null) {
            customer.setMarketingNotifications(request.marketingNotifications());
        }
        
        userRepository.save(user);
        customerRepository.save(customer);
        
        log.info("Customer profile updated: {}", username);
        
        return toProfileDTO(customer, user);
    }
    
    @Transactional(readOnly = true)
    public List<CustomerAddressDTO> getAddresses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user: " + username));
        
        return customer.getAddresses().stream()
                .map(this::toAddressDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CustomerAddressDTO createAddress(String username, CreateAddressRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user: " + username));
        
        CustomerAddress address = CustomerAddress.builder()
                .label(request.label())
                .street(request.street())
                .number(request.number())
                .complement(request.complement())
                .neighborhood(request.neighborhood())
                .city(request.city())
                .state(request.state())
                .zipCode(request.zipCode())
                .isDefault(request.isDefault() != null ? request.isDefault() : false)
                .build();
        
        customer.addAddress(address);
        
        if (Boolean.TRUE.equals(request.isDefault())) {
            customer.setDefaultAddress(address.getId());
        }
        
        customer = customerRepository.save(customer);
        
        log.info("Address created for customer: {}", username);
        
        return toAddressDTO(address);
    }
    
    @Transactional
    public CustomerAddressDTO updateAddress(UUID addressId, CreateAddressRequest request) {
        CustomerAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", addressId));
        
        address.setLabel(request.label());
        address.setStreet(request.street());
        address.setNumber(request.number());
        address.setComplement(request.complement());
        address.setNeighborhood(request.neighborhood());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());
        
        if (Boolean.TRUE.equals(request.isDefault())) {
            address.getCustomer().setDefaultAddress(addressId);
        }
        
        address = addressRepository.save(address);
        
        log.info("Address updated: {}", addressId);
        
        return toAddressDTO(address);
    }
    
    @Transactional
    public void deleteAddress(UUID addressId) {
        CustomerAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", addressId));
        
        address.getCustomer().removeAddress(address);
        addressRepository.delete(address);
        
        log.info("Address deleted: {}", addressId);
    }
    
    @Transactional
    public void addToFavorites(String username, UUID productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user: " + username));
        
        customer.addFavorite(productId);
        customerRepository.save(customer);
        
        log.info("Product {} added to favorites by {}", productId, username);
    }
    
    @Transactional
    public void removeFromFavorites(String username, UUID productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
        
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user: " + username));
        
        customer.removeFavorite(productId);
        customerRepository.save(customer);
        
        log.info("Product {} removed from favorites by {}", productId, username);
    }
    
    private Customer createDefaultCustomer(User user) {
        Customer customer = Customer.builder()
                .user(user)
                .newsletterSubscribed(false)
                .marketingNotifications(false)
                .build();
        
        return customerRepository.save(customer);
    }
    
    private CustomerProfileDTO toProfileDTO(Customer customer, User user) {
        List<CustomerAddressDTO> addresses = customer.getAddresses().stream()
                .map(this::toAddressDTO)
                .collect(Collectors.toList());
        
        return CustomerProfileDTO.builder()
                .id(customer.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .cpf(customer.getCpf())
                .birthDate(customer.getBirthDate())
                .phone(customer.getPhone())
                .addresses(addresses)
                .newsletterSubscribed(customer.getNewsletterSubscribed())
                .marketingNotifications(customer.getMarketingNotifications())
                .build();
    }
    
    private CustomerAddressDTO toAddressDTO(CustomerAddress address) {
        return CustomerAddressDTO.builder()
                .id(address.getId())
                .label(address.getLabel())
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .isDefault(address.getIsDefault())
                .formattedAddress(address.getFormattedAddress())
                .build();
    }
}