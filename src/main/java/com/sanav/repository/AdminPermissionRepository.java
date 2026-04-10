package com.sanav.repository;

import com.sanav.entity.AdminPermission;
import com.sanav.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminPermissionRepository extends JpaRepository<AdminPermission, Long> {
    Optional<AdminPermission> findByUser(User user);
    Optional<AdminPermission> findByUserId(Long userId);
}
