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
        // Main Admin account
        createAdminIfMissing("admin@sanav.com", "Laxmi Admin", "Admin@123");
        
        // Personal Admin account (as seen in user screenshot)
        createAdminIfMissing("mylaxmi@gmail.com", "Laxmi Personal", "Admin@123");
    }

    private void createAdminIfMissing(String email, String name, String password) {
        Optional<User> adminOptional = userRepository.findByEmail(email);
        if (adminOptional.isEmpty()) {
            User admin = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .build();
            userRepository.save(admin);
            System.out.println(">>> SEEDER: Created admin: " + email + " / " + password);
        } else {
            System.out.println(">>> SEEDER: Admin account already exists: " + email);
        }
    }
}
