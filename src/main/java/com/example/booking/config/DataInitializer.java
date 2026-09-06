package com.example.booking.config;

import com.example.booking.entity.AppUser;
import com.example.booking.entity.BookableResource;
import com.example.booking.entity.Role;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ResourceRepository resourceRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            AppUser admin = AppUser.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            AppUser user = AppUser.builder()
                    .username("user")
                    .password(passwordEncoder.encode("User@123"))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        }

        if (resourceRepository.count() == 0) {
            BookableResource r1 = BookableResource.builder()
                    .name("Conference Room A")
                    .description("Room with projector")
                    .type("ROOM")
                    .active(true)
                    .defaultPrice(new BigDecimal("100.00"))
                    .build();
            
            BookableResource r2 = BookableResource.builder()
                    .name("Company Car")
                    .description("Sedan")
                    .type("VEHICLE")
                    .active(true)
                    .defaultPrice(new BigDecimal("50.00"))
                    .build();
            
            resourceRepository.save(r1);
            resourceRepository.save(r2);
        }
    }
}
