package com.sanav.controller;

import com.sanav.entity.Product;
import com.sanav.response.ApiResponse;
import com.sanav.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import com.sanav.entity.Status;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<ApiResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved", productService.getAllProducts(pageable)));
    }

    private final String UPLOAD_DIR = "uploads/";

    private String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) directory.mkdirs();
        
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains(".")) ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String filename = UUID.randomUUID().toString() + extension;
        Path path = Paths.get(UPLOAD_DIR + filename);
        Files.write(path, file.getBytes());
        return "/uploads/" + filename;
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createProduct(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam("price") Double price,
            @RequestParam(value = "status", defaultValue = "ACTIVE") String status,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setCategory(category);
            product.setPrice(price);
            product.setStatus(Status.valueOf(status.toUpperCase()));
            
            String imageUrl = saveImage(image);
            if (imageUrl != null) product.setImageUrl(imageUrl);
            
            return ResponseEntity.ok(ApiResponse.success("Product created", productService.createProduct(product)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam("price") Double price,
            @RequestParam(value = "status", defaultValue = "ACTIVE") String status,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setCategory(category);
            product.setPrice(price);
            product.setStatus(Status.valueOf(status.toUpperCase()));
            
            if (image != null && !image.isEmpty()) {
                String imageUrl = saveImage(image);
                product.setImageUrl(imageUrl);
            }
            // Existing image mapping is handled in the Service layer if imageUrl is null here.
            
            return ResponseEntity.ok(ApiResponse.success("Product updated", productService.updateProduct(id, product)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }
}
