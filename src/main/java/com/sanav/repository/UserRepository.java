package com.sanav.repository;

import com.sanav.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import com.sanav.entity.Role;
import com.sanav.entity.Status;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Page<User> findByNameContainingOrEmailContaining(String name, String email, Pageable pageable);
    Page<User> findByRole(Role role, Pageable pageable);
    Page<User> findByStatus(Status status, Pageable pageable);
    long count();
    List<User> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :start AND u.createdAt < :end")
    long countByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

