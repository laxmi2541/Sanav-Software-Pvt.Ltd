package com.sanav.service;

import com.sanav.entity.Product;
import com.sanav.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<Product> getAllProducts(Pageable pageable);
    Page<Product> searchProducts(String name, Pageable pageable);
    Page<Product> filterProductsByCategory(String category, Pageable pageable);
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    Product changeProductStatus(Long id, Status status);
    List<Product> getRecentProducts();
}
