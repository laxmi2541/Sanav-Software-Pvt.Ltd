package com.sanav.repository;

import com.sanav.entity.LoginHistory;
import com.sanav.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByUserOrderByLoginTimeDesc(User user);
    List<LoginHistory> findTop10ByOrderByLoginTimeDesc();
}
