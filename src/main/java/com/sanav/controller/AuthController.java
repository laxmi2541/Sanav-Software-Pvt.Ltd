package com.sanav.controller;

import com.sanav.dto.ChangePasswordRequest;
import com.sanav.dto.LoginRequest;
import com.sanav.dto.RegisterRequest;
import com.sanav.entity.User;
import com.sanav.response.ApiResponse;
import com.sanav.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Success: User " + user.getName() + " registered and saved to database.", user));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/auth/admin-login")
    public ResponseEntity<ApiResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> response = authService.login(request);
        // Requirement 1: Ensure only ADMIN can log in through here
        @SuppressWarnings("unchecked")
        Map<String, Object> userData = (Map<String, Object>) response.get("user");
        if (userData == null || userData.get("role") == null || !"ADMIN".equals(userData.get("role").toString())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied: You do not have admin privileges."));
        }
        return ResponseEntity.ok(ApiResponse.success("Admin login successful", response));
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            String token = authService.forgotPassword(email);
            // Simulate sending token in response for testing
            return ResponseEntity.ok(ApiResponse.success("If an account exists for " + email + ", a reset link has been sent.", "http://localhost:5500/reset-password.html?token=" + token));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success("If an account exists for " + email + ", a reset link has been sent.", null));
        }
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");
        try {
            authService.resetPassword(token, newPassword);
            return ResponseEntity.ok(ApiResponse.success("Password Changed Successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid or expired token"));
        }
    }

    @PostMapping("/auth/change-password")
    public ResponseEntity<ApiResponse> changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> passwords) {
        authService.changePassword(userDetails.getUsername(), passwords.get("oldPassword"), passwords.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @PutMapping("/auth/update-profile")
    public ResponseEntity<ApiResponse> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody RegisterRequest request) {
        User updatedUser = authService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully, please clear localStorage.", null));
    }

    @PostMapping("/auth/admin-forgot-password")
    public ResponseEntity<ApiResponse> adminForgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            String token = authService.adminForgotPassword(email);
            return ResponseEntity.ok(ApiResponse.success("If an admin account exists for " + email + ", a reset link has been sent.", "http://localhost:5500/admin-reset-password.html?token=" + token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/auth/admin-reset-password")
    public ResponseEntity<ApiResponse> adminResetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");
        try {
            authService.adminResetPassword(token, newPassword);
            return ResponseEntity.ok(ApiResponse.success("Admin Password Changed Successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/auth/admin-change-password")
    public ResponseEntity<ApiResponse> adminChangePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> passwords) {
        try {
            authService.adminChangePassword(userDetails.getUsername(), passwords.get("oldPassword"), passwords.get("newPassword"));
            return ResponseEntity.ok(ApiResponse.success("Admin Password changed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
