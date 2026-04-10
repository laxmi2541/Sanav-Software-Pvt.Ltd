package com.sanav.service;

import com.sanav.dto.LoginRequest;
import com.sanav.dto.RegisterRequest;
import com.sanav.entity.User;

import java.util.Map;

public interface AuthService {
    User register(RegisterRequest request);
    Map<String, Object> login(LoginRequest request);
    User getUserByEmail(String email);
    User updateProfile(String email, RegisterRequest request);
    void changePassword(String email, String oldPassword, String newPassword);
    String forgotPassword(String email);
    void resetPassword(String token, String newPassword);
    String adminForgotPassword(String email);
    void adminResetPassword(String token, String newPassword);
    void adminChangePassword(String email, String oldPassword, String newPassword);
}
