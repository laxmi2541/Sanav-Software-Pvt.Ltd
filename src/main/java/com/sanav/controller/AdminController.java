package com.sanav.controller;

import com.sanav.entity.*;
import com.sanav.repository.*;
import com.sanav.response.ApiResponse;
import com.sanav.service.ContactMessageService;
import com.sanav.service.EmailService;
import com.sanav.service.ProductService;
import com.sanav.service.UserService;
import com.sanav.util.CsvExporter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.sanav.service.AuthService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private ProductService productService;
    @Autowired private ContactMessageService messageService;
    @Autowired private EmailService emailService;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ContactMessageRepository contactMessageRepository;
    @Autowired private DemoRequestRepository demoRequestRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private AuthService authService;

    private void logAction(String action, String targetType, Long targetId, String details) {
        try {
            // Get current admin name from SecurityContext if possible, or use "Admin"
            String adminName = "Admin"; 
            User admin = userRepository.findByEmail(adminName).orElse(null);

            AuditLog log = AuditLog.builder()
                    .action(action)
                    .targetType(targetType)
                    .targetId(targetId)
                    .details(details)
                    .admin(admin)
                    .build();
            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Failed to log audit action: " + e.getMessage());
        }
    }

    private static final String UPLOAD_DIR = "uploads/";

    // =========================================================
    // DASHBOARD
    // =========================================================

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboard() {
        Map<String, Object> data = new HashMap<>();

        data.put("totalUsers", userRepository.count());
        data.put("totalProducts", productRepository.countByIsDeletedFalse());
        data.put("totalMessages", contactMessageRepository.count());
        data.put("totalDemoRequests", demoRequestRepository.count());

        // New users registered today
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        data.put("newUsersToday", userRepository.countByCreatedAtBetween(startOfDay, endOfDay));

        // Active users
        long activeUsers = userRepository.findAll().stream()
                .filter(u -> u.getStatus() == Status.ACTIVE).count();
        data.put("activeUsers", activeUsers);

        // Unread messages
        data.put("unreadMessages", contactMessageRepository.countByStatus(MessageStatus.UNREAD));

        // Recent data
        data.put("recentUsers", userRepository.findTop5ByOrderByCreatedAtDesc());
        data.put("latestMessages", contactMessageRepository.findTop5ByOrderByCreatedAtDesc());
        data.put("recentProducts", productRepository.findTop5ByIsDeletedFalseOrderByCreatedAtDesc());

        return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", data));
    }

    // =========================================================
    // USER MANAGEMENT - /api/admin/users/**
    // =========================================================

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success("Users searched", userService.searchUsers(search, pageable)));
        }
        if (role != null && !role.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success("Users filtered by role", userService.filterUsersByRole(Role.valueOf(role.toUpperCase()), pageable)));
        }
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", userService.getAllUsers(pageable)));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User retrieved", userService.getUserById(id)));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        logAction("CREATE_USER", "USER", created.getId(), "Created user: " + created.getEmail());
        return ResponseEntity.ok(ApiResponse.success("User created", created));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        logAction("UPDATE_USER", "USER", id, "Updated user: " + updated.getEmail());
        return ResponseEntity.ok(ApiResponse.success("User updated", updated));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        userService.deleteUser(id);
        logAction("DELETE_USER", "USER", id, "Deleted user: " + user.getEmail());
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse> changeUserRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Role role = Role.valueOf(body.get("role").toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("Role updated", userService.changeUserRole(id, role)));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse> changeUserStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Status status = Status.valueOf(body.get("status").toUpperCase());
        User updated = userService.changeUserStatus(id, status);
        logAction("CHANGE_USER_STATUS", "USER", id, "Status changed to " + status + " for " + updated.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Status updated", updated));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> adminChangePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> passwords) {
        try {
            authService.adminChangePassword(userDetails.getUsername(), passwords.get("oldPassword"), passwords.get("newPassword"));
            logAction("CHANGE_PASSWORD", "USER", null, "Admin changed their own password: " + userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Admin Password changed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/export/csv")
    public ResponseEntity<byte[]> exportUsersCsv() {
        byte[] csvBytes = CsvExporter.generateUsersCsv(userRepository.findAll());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "users.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }

    // =========================================================
    // PRODUCT MANAGEMENT - /api/admin/products/**
    // =========================================================

    @GetMapping("/products")
    public ResponseEntity<ApiResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Products retrieved", productService.getAllProducts(pageable)));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Product retrieved", productService.getProductById(id)));
    }

    @GetMapping("/products/search")
    public ResponseEntity<ApiResponse> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Products searched", productService.searchProducts(keyword, pageable)));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse> createProduct(@RequestBody @Valid com.sanav.dto.ProductRequest request) {
        try {
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setCategory(request.getCategory());
            product.setPrice(request.getPrice());
            product.setImageUrl(request.getImageUrl());
            if (request.getStatus() != null) product.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
            
            Product created = productService.createProduct(product);
            logAction("CREATE_PRODUCT", "PRODUCT", created.getId(), "Created product: " + created.getName());
            return ResponseEntity.ok(ApiResponse.success("Product created", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid com.sanav.dto.ProductRequest request) {
        try {
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setCategory(request.getCategory());
            product.setPrice(request.getPrice());
            product.setImageUrl(request.getImageUrl());
            if (request.getStatus() != null) product.setStatus(Status.valueOf(request.getStatus().toUpperCase()));

            Product updated = productService.updateProduct(id, product);
            logAction("UPDATE_PRODUCT", "PRODUCT", id, "Updated product: " + updated.getName());
            return ResponseEntity.ok(ApiResponse.success("Product updated", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        Product p = productService.getProductById(id);
        productService.deleteProduct(id); // Soft delete
        logAction("DELETE_PRODUCT", "PRODUCT", id, "Soft-deleted product: " + p.getName());
        return ResponseEntity.ok(ApiResponse.success("Product deleted (soft)", null));
    }

    @PatchMapping("/products/{id}/status")
    public ResponseEntity<ApiResponse> changeProductStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Status status = Status.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("Product status updated", productService.changeProductStatus(id, status)));
    }

    @GetMapping("/products/export/csv")
    public ResponseEntity<byte[]> exportProductsCsv() {
        byte[] csvBytes = CsvExporter.generateProductsCsv(productRepository.findAll());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "products.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }

    // =========================================================
    // MESSAGE MANAGEMENT - /api/admin/messages/**
    // =========================================================

    @GetMapping("/messages")
    public ResponseEntity<ApiResponse> getAllMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success("Messages searched", messageService.searchMessages(search, pageable)));
        }
        return ResponseEntity.ok(ApiResponse.success("Messages retrieved", messageService.getAllMessages(pageable)));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<ApiResponse> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Message retrieved", messageService.getMessageById(id)));
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<ApiResponse> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok(ApiResponse.success("Message deleted", null));
    }

    @PatchMapping("/messages/{id}/read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Marked as read", messageService.markAsRead(id, MessageStatus.READ)));
    }

    @PatchMapping("/messages/{id}/unread")
    public ResponseEntity<ApiResponse> markAsUnread(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Marked as unread", messageService.markAsRead(id, MessageStatus.UNREAD)));
    }

    @PostMapping("/messages/{id}/reply")
    public ResponseEntity<ApiResponse> replyToMessage(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        ContactMessage message = messageService.getMessageById(id);
        String replyBody = payload.get("reply");
        emailService.sendReplyEmail(message.getEmail(), message.getSubject(), replyBody);
        messageService.markAsRead(id, MessageStatus.READ);
        logAction("REPLY_MESSAGE", "MESSAGE", id, "Replied to message from: " + message.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Reply sent to " + message.getEmail(), null));
    }

    @GetMapping("/messages/export/csv")
    public ResponseEntity<byte[]> exportMessagesCsv() {
        byte[] csvBytes = CsvExporter.generateMessagesCsv(contactMessageRepository.findAll());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "messages.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }

    // =========================================================
    // AUDIT LOGS
    // =========================================================

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved", auditLogRepository.findAll(pageable)));
    }

    // =========================================================
    // FILE UPLOAD HELPER
    // =========================================================

    private String saveImage(MultipartFile file) throws IOException {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) directory.mkdirs();
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String filename = UUID.randomUUID() + extension;
        Path path = Paths.get(UPLOAD_DIR + filename);
        Files.write(path, file.getBytes());
        return "/uploads/" + filename;
    }
}
