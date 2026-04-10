package com.sanav.repository;

import com.sanav.entity.Product;
import com.sanav.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Soft-delete aware queries
    Page<Product> findByIsDeletedFalse(Pageable pageable);
    Page<Product> findByNameContainingAndIsDeletedFalse(String name, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, Pageable pageable);
    Page<Product> findByCategoryAndIsDeletedFalse(String category, Pageable pageable);
    Page<Product> findByStatusAndIsDeletedFalse(Status status, Pageable pageable);
    List<Product> findTop5ByIsDeletedFalseOrderByCreatedAtDesc();
    long countByIsDeletedFalse();
    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    // Legacy queries (kept for backward compatibility)
    Page<Product> findByNameContaining(String name, Pageable pageable);
    Page<Product> findByCategory(String category, Pageable pageable);
    Page<Product> findByStatus(Status status, Pageable pageable);
    List<Product> findTop5ByOrderByCreatedAtDesc();
}

