package com.sanav.config;

import com.sanav.entity.*;
import com.sanav.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private com.sanav.repository.ServiceRepository serviceRepository;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Seed default admin if not present
        if (!userRepository.existsByEmail("admin@sanav.com")) {
            User admin = User.builder()
                    .name("Sanav Admin")
                    .email("admin@sanav.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .build();
            userRepository.save(admin);
            System.out.println("=== Default admin created: admin@sanav.com / Admin@123 ===");
        }


        // Add sample products if table is empty
        if (productRepository.count() == 0) {
            productRepository.save(Product.builder().name("Custom ERP").description("Enterprise Resource Planning software").price(1500.0).status(Status.ACTIVE).imageUrl("images/product1.jpg").build());
            productRepository.save(Product.builder().name("Mobile App").description("Android and iOS applications").price(800.0).status(Status.ACTIVE).imageUrl("images/product2.jpg").build());
            productRepository.save(Product.builder().name("CRM Tool").description("Customer Relationship Management").price(1200.0).status(Status.ACTIVE).imageUrl("images/product3.jpg").build());
            System.out.println("Sample products created.");
        }

        // Add sample services if table is empty
        if (serviceRepository.count() == 0) {
            serviceRepository.save(Service.builder().title("Web Development").description("High quality web applications").icon("fas fa-laptop-code").build());
            serviceRepository.save(Service.builder().title("Industrial Training").description("Summer and Winter internships").icon("fas fa-graduation-cap").build());
            serviceRepository.save(Service.builder().title("IT Consulting").description("Professional technology advice").icon("fas fa-users").build());
            System.out.println("Sample services created.");
        }

        // Add sample contact messages if table is empty
        if (contactMessageRepository.count() == 0) {
            contactMessageRepository.save(ContactMessage.builder().name("John Doe").email("john@example.com").subject("Inquiry").message("Interested in your ERP solutions.").build());
            contactMessageRepository.save(ContactMessage.builder().name("Jane Smith").email("jane@example.com").subject("Job Opening").message("Are you hiring for Java developers?").build());
            System.out.println("Sample contact messages created.");
        }
    }
}
