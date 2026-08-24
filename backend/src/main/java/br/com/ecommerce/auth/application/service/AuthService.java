package br.com.ecommerce.auth.application.service;

import br.com.ecommerce.auth.application.dto.*;
import br.com.ecommerce.auth.domain.entity.Role;
import br.com.ecommerce.auth.domain.entity.User;
import br.com.ecommerce.auth.domain.repository.RoleRepository;
import br.com.ecommerce.auth.domain.repository.UserRepository;
import br.com.ecommerce.shared.exception.BusinessException;
import br.com.ecommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.usernameOrEmail(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        // Update last login
        User user = userRepository.findByUsername(authentication.getName())
                .or(() -> userRepository.findByEmail(authentication.getName()))
                .orElseThrow(() -> new ResourceNotFoundException("User", authentication.getName()));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate username and email uniqueness
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email is already in use");
        }

        // Get default CUSTOMER role
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .name("ROLE_CUSTOMER")
                            .description("Customer role")
                            .build();
                    return roleRepository.save(newRole);
                });

        // Create user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .active(true)
                .emailVerified(false)
                .passwordChangedAt(LocalDateTime.now())
                .build();

        user.addRole(customerRole);
        user = userRepository.save(user);

        log.info("New user registered: {}", user.getUsername());

        // Auto-login after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        String accessToken = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.validateToken(refreshToken)) {
            throw new BusinessException("Invalid refresh token");
        }

        String username = jwtService.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                user.getRoles().stream()
                        .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toSet())
        );

        String newAccessToken = jwtService.generateToken(authentication);
        String newRefreshToken = jwtService.generateRefreshToken(authentication);

        return buildAuthResponse(newAccessToken, newRefreshToken, user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));

        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password changed for user: {}", username);
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roles)
                .lastLogin(user.getLastLogin())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpiration())
                .user(userInfo)
                .build();
    }
}