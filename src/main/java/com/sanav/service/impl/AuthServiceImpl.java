package com.sanav.service.impl;

import java.time.LocalDateTime;

import com.sanav.dto.LoginRequest;
import com.sanav.dto.RegisterRequest;
import com.sanav.entity.Role;
import com.sanav.entity.User;
import com.sanav.exception.DuplicateEmailException;
import com.sanav.exception.ResourceNotFoundException;
import com.sanav.repository.UserRepository;
import com.sanav.security.JwtUtil;
import com.sanav.service.AuthService;
import com.sanav.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.sanav.repository.PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + request.getEmail());
        }

        // Hardcode USER role for all public registrations to prevent unauthorized ADMIN creation
        Role role = Role.USER;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .status(com.sanav.entity.Status.ACTIVE)
                .build();

        return userRepository.save(user);
    }


    @Override
    public Map<String, Object> login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole());
        
        response.put("user", userData);
        
        return response;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    public User updateProfile(String email, RegisterRequest request) {
        User user = getUserByEmail(email);
        user.setName(request.getName());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = getUserByEmail(email);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        String token = java.util.UUID.randomUUID().toString();
        
        // Remove old token if exists
        tokenRepository.deleteByUser(user);
        
        com.sanav.entity.PasswordResetToken resetToken = com.sanav.entity.PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        tokenRepository.save(resetToken);
        
        return token;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        com.sanav.entity.PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));
        
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Reset token has expired");
        }
        
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        tokenRepository.delete(resetToken);
    }

    private void validateStrongPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!password.matches(regex)) {
            throw new RuntimeException("Password must contain at least 8 characters, an uppercase, a lowercase, a number and a special character.");
        }
    }

    @Override
    public String adminForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("User is not an Administrator.");
        }

        String token = java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // OTP
        
        tokenRepository.deleteByUser(user);
        com.sanav.entity.PasswordResetToken resetToken = com.sanav.entity.PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(resetToken);

        emailService.sendResetEmail(user.getEmail(), token);
        return token;
    }

    @Override
    public void adminResetPassword(String token, String newPassword) {
        com.sanav.entity.PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getUser().getRole() != Role.ADMIN) {
            throw new RuntimeException("Unauthorized");
        }

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token has expired");
        }

        validateStrongPassword(newPassword);

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        tokenRepository.delete(resetToken);
    }


    @Override
    public void adminChangePassword(String email, String oldPassword, String newPassword) {
        User user = getUserByEmail(email);
        
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Unauthorized");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Invalid current password");
        }

        validateStrongPassword(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
