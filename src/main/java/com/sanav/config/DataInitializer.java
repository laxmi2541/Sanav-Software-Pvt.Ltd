package com.sanav.config;

import com.sanav.entity.Role;
import com.sanav.entity.User;
import com.sanav.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Promote mylaxmi@gmail.com to ADMIN
        promoteToAdmin("mylaxmi@gmail.com", "Laxmi Admin");

        // 2. Create default ADMIN (admin@gmail.com) if not exists
        createAdminIfNotExists("admin@gmail.com", "Admin", "admin123");
    }

    private void promoteToAdmin(String email, String name) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getRole() != Role.ADMIN) {
                user.setRole(Role.ADMIN);
                userRepository.save(user);
                System.out.println("User " + email + " promoted to ADMIN.");
            }
        } else {
            // Create if does not exist (optional, but requested)
            User admin = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode("admin123")) // Default password if new
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("New Admin account created for " + email);
        }
    }

    private void createAdminIfNotExists(String email, String name, String password) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User admin = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Default Admin account created: " + email);
        }
    }
}
