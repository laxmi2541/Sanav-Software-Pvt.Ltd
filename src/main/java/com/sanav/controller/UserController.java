package com.sanav.controller;

import com.sanav.entity.Role;
import com.sanav.entity.Status;
import com.sanav.entity.User;
import com.sanav.response.ApiResponse;
import com.sanav.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role) {
        
        Pageable pageable = PageRequest.of(page, size);
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("Users searched", userService.searchUsers(search, pageable)));
        } else if (role != null) {
            return ResponseEntity.ok(ApiResponse.success("Users filtered by role", userService.filterUsersByRole(role, pageable)));
        }
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", userService.getAllUsers(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User retrieved", userService.getUserById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody User user) {
        return ResponseEntity.ok(ApiResponse.success("User created", userService.createUser(user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(ApiResponse.success("User updated", userService.updateUser(id, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse> changeRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Role role = Role.valueOf(request.get("role").toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("Role changed", userService.changeUserRole(id, role)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> changeStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Status status = Status.valueOf(request.get("status").toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("Status changed", userService.changeUserStatus(id, status)));
    }
    
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse> resetUserPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("password");
        User user = userService.getUserById(id);
        user.setPassword(newPassword); // We'll let the user service update method handle encoding
        return ResponseEntity.ok(ApiResponse.success("Password manually reset by admin", userService.updateUser(id, user)));
    }
}
