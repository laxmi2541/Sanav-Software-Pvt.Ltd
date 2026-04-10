package com.sanav.service.impl;

import com.sanav.entity.AuditLog;
import com.sanav.entity.Role;
import com.sanav.entity.Status;
import com.sanav.entity.User;
import com.sanav.exception.ResourceNotFoundException;
import com.sanav.repository.AuditLogRepository;
import com.sanav.repository.UserRepository;
import com.sanav.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> searchUsers(String query, Pageable pageable) {
        return userRepository.findByNameContainingOrEmailContaining(query, query, pageable);
    }

    @Override
    public Page<User> filterUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole(Role.USER);
        if (user.getStatus() == null) user.setStatus(Status.ACTIVE);
        
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setStatus(userDetails.getStatus());
        
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        auditLogRepository.save(AuditLog.builder()
                .action("EDIT_USER")
                .targetType("USER")
                .targetId(id)
                .details("Updated details for " + user.getEmail())
                .build());
                
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        auditLogRepository.save(AuditLog.builder()
                .action("DELETE_USER")
                .targetType("USER")
                .targetId(id)
                .details("Deleted user: " + user.getEmail())
                .build());
        userRepository.delete(user);
    }

    @Override
    public User changeUserRole(Long id, Role role) {
        User user = getUserById(id);
        user.setRole(role);
        
        auditLogRepository.save(AuditLog.builder()
                .action("CHANGE_ROLE")
                .targetType("USER")
                .targetId(id)
                .details("Changed role of " + user.getEmail() + " to " + role)
                .build());
                
        return userRepository.save(user);
    }

    @Override
    public User changeUserStatus(Long id, Status status) {
        User user = getUserById(id);
        user.setStatus(status);
        
        auditLogRepository.save(AuditLog.builder()
                .action("CHANGE_STATUS")
                .targetType("USER")
                .targetId(id)
                .details("Changed status of " + user.getEmail() + " to " + status)
                .build());
                
        return userRepository.save(user);
    }

    @Override
    public List<User> getRecentUsers() {
        return userRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    public long countNewUsersToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return userRepository.countByCreatedAtBetween(startOfDay, endOfDay);
    }
}

