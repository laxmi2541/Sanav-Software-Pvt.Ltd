package com.sanav.repository;

import com.sanav.entity.DemoRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoRequestRepository extends JpaRepository<DemoRequest, Long> {
}
