package com.sanav.controller;

import com.sanav.entity.MessageStatus;
import com.sanav.entity.Status;
import com.sanav.repository.AuditLogRepository;
import com.sanav.repository.ContactMessageRepository;
import com.sanav.repository.ProductRepository;
import com.sanav.repository.UserRepository;
import com.sanav.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalProducts", productRepository.count());
        stats.put("totalMessages", contactMessageRepository.count());
        
        long activeUsers = userRepository.findAll().stream().filter(u -> u.getStatus() == Status.ACTIVE).count();
        stats.put("activeUsers", activeUsers);
        
        stats.put("unreadMessages", contactMessageRepository.countByStatus(MessageStatus.UNREAD));
        
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", stats));
    }

    @GetMapping("/recent-users")
    public ResponseEntity<ApiResponse> getRecentUsers() {
        return ResponseEntity.ok(ApiResponse.success("Recent users retrieved", userRepository.findTop5ByOrderByCreatedAtDesc()));
    }

    @GetMapping("/recent-products")
    public ResponseEntity<ApiResponse> getRecentProducts() {
        return ResponseEntity.ok(ApiResponse.success("Recent products retrieved", productRepository.findTop5ByOrderByCreatedAtDesc()));
    }

    @GetMapping("/recent-messages")
    public ResponseEntity<ApiResponse> getRecentMessages() {
        return ResponseEntity.ok(ApiResponse.success("Recent messages retrieved", contactMessageRepository.findTop5ByOrderByCreatedAtDesc()));
    }

    @GetMapping("/charts")
    public ResponseEntity<ApiResponse> getChartsData() {
        Map<String, Object> charts = new HashMap<>();
        
        // Mock data aggregation for charts if JPA Group By isn't setup.
        // Product Categories Pie Chart
        Map<String, Long> categoryCount = new HashMap<>();
        productRepository.findAll().forEach(p -> {
            categoryCount.put(p.getCategory(), categoryCount.getOrDefault(p.getCategory(), 0L) + 1);
        });
        charts.put("productCategories", categoryCount);

        // This could be filled with actual time-series logic later
        charts.put("userGrowth", "Data aggregation pending time-series configuration");
        charts.put("messagesPerDay", "Data aggregation pending time-series configuration");

        return ResponseEntity.ok(ApiResponse.success("Chart data retrieved", charts));
    }
}
