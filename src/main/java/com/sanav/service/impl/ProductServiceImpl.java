package com.sanav.service.impl;

import com.sanav.entity.AuditLog;
import com.sanav.entity.Product;
import com.sanav.entity.Status;
import com.sanav.exception.ResourceNotFoundException;
import com.sanav.repository.AuditLogRepository;
import com.sanav.repository.ProductRepository;
import com.sanav.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByIsDeletedFalse(pageable);
    }

    @Override
    public Page<Product> searchProducts(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name, pageable);
    }

    @Override
    public Page<Product> filterProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryAndIsDeletedFalse(category, pageable);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    @Override
    public Product createProduct(Product product) {
        if (product.getStatus() == null) product.setStatus(Status.ACTIVE);
        product.setIsDeleted(false);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setCategory(productDetails.getCategory());
        product.setPrice(productDetails.getPrice());
        if (productDetails.getImageUrl() != null) {
            product.setImageUrl(productDetails.getImageUrl());
        }
        product.setStatus(productDetails.getStatus());

        auditLogRepository.save(AuditLog.builder()
                .action("EDIT_PRODUCT")
                .targetType("PRODUCT")
                .targetId(id)
                .details("Updated product: " + product.getName())
                .build());

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        // Soft delete: mark as deleted instead of removing from DB
        product.setIsDeleted(true);
        productRepository.save(product);

        auditLogRepository.save(AuditLog.builder()
                .action("DELETE_PRODUCT")
                .targetType("PRODUCT")
                .targetId(id)
                .details("Soft-deleted product: " + product.getName())
                .build());
    }

    @Override
    public Product changeProductStatus(Long id, Status status) {
        Product product = getProductById(id);
        product.setStatus(status);

        auditLogRepository.save(AuditLog.builder()
                .action("CHANGE_PRODUCT_STATUS")
                .targetType("PRODUCT")
                .targetId(id)
                .details("Changed status of product " + product.getName() + " to " + status)
                .build());

        return productRepository.save(product);
    }

    @Override
    public List<Product> getRecentProducts() {
        return productRepository.findTop5ByIsDeletedFalseOrderByCreatedAtDesc();
    }
}
