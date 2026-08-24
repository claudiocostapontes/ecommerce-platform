package br.com.ecommerce.config;

import br.com.ecommerce.auth.domain.entity.Role;
import br.com.ecommerce.auth.domain.entity.User;
import br.com.ecommerce.auth.domain.repository.RoleRepository;
import br.com.ecommerce.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        Role adminRole = getOrCreateRole(
                "ROLE_ADMIN",
                "Administrador com acesso total"
        );

        getOrCreateRole(
                "ROLE_MANAGER",
                "Gerente da operação"
        );

        getOrCreateRole(
                "ROLE_OPERATOR",
                "Operador de pedidos e estoque"
        );

        getOrCreateRole(
                "ROLE_CUSTOMER",
                "Cliente da loja"
        );

        if (!userRepository.existsByUsername("admin")) {

            User admin = User.builder()
                    .username("admin")
                    .email("admin@ecommerce.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .firstName("Administrador")
                    .lastName("E-commerce")
                    .active(true)
                    .emailVerified(true)
                    .passwordChangedAt(LocalDateTime.now())
                    .build();

            admin.addRole(adminRole);

            userRepository.save(admin);

            log.warn(
                    "Usuário administrativo DEV criado: admin@ecommerce.com. " +
                            "Altere/remova a credencial de desenvolvimento antes de produção."
            );
        }
    }

    private Role getOrCreateRole(
            String name,
            String description
    ) {

        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(name)
                                .description(description)
                                .build()
                ));
    }
}