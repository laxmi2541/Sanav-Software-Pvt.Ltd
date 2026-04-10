package com.sanav.config;

import com.sanav.entity.Role;
import com.sanav.entity.Status;
import com.sanav.entity.User;
import com.sanav.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Ensure at least one admin exists
        String adminEmail = "admin@sanav.com";
        Optional<User> adminOptional = userRepository.findByEmail(adminEmail);

        if (adminOptional.isEmpty()) {
            User admin = User.builder()
                    .name("Laxmi Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .build();
            userRepository.save(admin);
            System.out.println(">>> SEEDER: Created default admin: " + adminEmail + " / Admin@123");
        } else {
            System.out.println(">>> SEEDER: Admin account already exists.");
        }
    }
}
