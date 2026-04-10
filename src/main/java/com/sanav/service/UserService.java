package com.sanav.service;

import com.sanav.entity.User;
import com.sanav.entity.Role;
import com.sanav.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<User> getAllUsers(Pageable pageable);
    Page<User> searchUsers(String query, Pageable pageable);
    Page<User> filterUsersByRole(Role role, Pageable pageable);
    User getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    User changeUserRole(Long id, Role role);
    User changeUserStatus(Long id, Status status);
    List<User> getRecentUsers();
    long countNewUsersToday();
}

