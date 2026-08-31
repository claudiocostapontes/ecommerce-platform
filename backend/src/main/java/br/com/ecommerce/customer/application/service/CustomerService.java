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

    @Transactional
    public CustomerProfileDTO getProfile(String username) {
        User user = getUser(username);

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseGet(() -> createDefaultCustomer(user));

        return toProfileDTO(customer, user);
    }

    @Transactional
    public CustomerProfileDTO updateProfile(
            String username,
            UpdateCustomerRequest request
    ) {
        User user = getUser(username);

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
            customer.setNewsletterSubscribed(
                    request.newsletterSubscribed()
            );
        }

        if (request.marketingNotifications() != null) {
            customer.setMarketingNotifications(
                    request.marketingNotifications()
            );
        }

        userRepository.save(user);
        customer = customerRepository.save(customer);

        log.info("Customer profile updated: {}", username);

        return toProfileDTO(customer, user);
    }

    @Transactional(readOnly = true)
    public List<CustomerAddressDTO> getAddresses(String username) {
        User user = getUser(username);
        Customer customer = getCustomer(user, username);

        return customer.getAddresses()
                .stream()
                .map(this::toAddressDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerAddressDTO createAddress(
            String username,
            CreateAddressRequest request
    ) {
        User user = getUser(username);
        Customer customer = getCustomer(user, username);

        CustomerAddress address = CustomerAddress.builder()
                .label(request.label())
                .street(request.street())
                .number(request.number())
                .complement(request.complement())
                .neighborhood(request.neighborhood())
                .city(request.city())
                .state(request.state())
                .zipCode(request.zipCode())
                .isDefault(Boolean.TRUE.equals(request.isDefault()))
                .build();

        customer.addAddress(address);

        customerRepository.saveAndFlush(customer);

        if (Boolean.TRUE.equals(request.isDefault())) {
            customer.setDefaultAddress(address.getId());
            customerRepository.save(customer);
        }

        log.info(
                "Address {} created for customer: {}",
                address.getId(),
                username
        );

        return toAddressDTO(address);
    }

    @Transactional
    public CustomerAddressDTO updateAddress(
            String username,
            UUID addressId,
            CreateAddressRequest request
    ) {
        User user = getUser(username);
        Customer customer = getCustomer(user, username);

        CustomerAddress address =
                getOwnedAddress(customer, addressId);

        address.setLabel(request.label());
        address.setStreet(request.street());
        address.setNumber(request.number());
        address.setComplement(request.complement());
        address.setNeighborhood(request.neighborhood());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());

        if (Boolean.TRUE.equals(request.isDefault())) {
            customer.setDefaultAddress(addressId);
        } else if (request.isDefault() != null) {
            address.setIsDefault(false);
        }

        address = addressRepository.save(address);

        log.info(
                "Address {} updated by customer: {}",
                addressId,
                username
        );

        return toAddressDTO(address);
    }

    @Transactional
    public void deleteAddress(
            String username,
            UUID addressId
    ) {
        User user = getUser(username);
        Customer customer = getCustomer(user, username);

        CustomerAddress address =
                getOwnedAddress(customer, addressId);

        customer.removeAddress(address);

        customerRepository.save(customer);

        log.info(
                "Address {} deleted by customer: {}",
                addressId,
                username
        );
    }

    @Transactional
    public void addToFavorites(
            String username,
            UUID productId
    ) {
        User user = getUser(username);
        Customer customer = getCustomer(user, username);

        customer.addFavorite(productId);
        customerRepository.save(customer);

        log.info(
                "Product {} added to favorites by {}",
                productId,
                username
        );
    }

    @Transactional
    public void removeFromFavorites(
            String username,
            UUID productId
    ) {
        User user = getUser(username);
        Customer customer = getCustomer(user, username);

        customer.removeFavorite(productId);
        customerRepository.save(customer);

        log.info(
                "Product {} removed from favorites by {}",
                productId,
                username
        );
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User",
                                username
                        )
                );
    }

    private Customer getCustomer(
            User user,
            String username
    ) {
        return customerRepository.findByUserId(user.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Customer not found for user: "
                                        + username
                        )
                );
    }

    private CustomerAddress getOwnedAddress(
            Customer customer,
            UUID addressId
    ) {
        return customer.getAddresses()
                .stream()
                .filter(address ->
                        addressId.equals(address.getId())
                )
                .findFirst()
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Address",
                                addressId
                        )
                );
    }

    private Customer createDefaultCustomer(User user) {
        Customer customer = Customer.builder()
                .user(user)
                .newsletterSubscribed(false)
                .marketingNotifications(false)
                .build();

        customer = customerRepository.save(customer);

        log.info(
                "Default customer profile created for user: {}",
                user.getUsername()
        );

        return customer;
    }

    private CustomerProfileDTO toProfileDTO(
            Customer customer,
            User user
    ) {
        List<CustomerAddressDTO> addresses =
                customer.getAddresses()
                        .stream()
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
                .newsletterSubscribed(
                        customer.getNewsletterSubscribed()
                )
                .marketingNotifications(
                        customer.getMarketingNotifications()
                )
                .build();
    }

    private CustomerAddressDTO toAddressDTO(
            CustomerAddress address
    ) {
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
                .formattedAddress(
                        address.getFormattedAddress()
                )
                .build();
    }
}