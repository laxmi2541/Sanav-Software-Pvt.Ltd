package com.sanav.controller;

import com.sanav.entity.Service;
import com.sanav.response.ApiResponse;
import com.sanav.service.ServiceModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ServiceController {

    @Autowired
    private ServiceModuleService serviceModuleService;

    @GetMapping("/services")
    public ResponseEntity<ApiResponse> getAllServices() {
        return ResponseEntity.ok(ApiResponse.success("Services retrieved", serviceModuleService.getAllServices()));
    }

    @PostMapping("/admin/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createService(@RequestBody Service service) {
        return ResponseEntity.ok(ApiResponse.success("Service created", serviceModuleService.createService(service)));
    }

    @PutMapping("/admin/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateService(@PathVariable Long id, @RequestBody Service service) {
        return ResponseEntity.ok(ApiResponse.success("Service updated", serviceModuleService.updateService(id, service)));
    }

    @DeleteMapping("/admin/services/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteService(@PathVariable Long id) {
        serviceModuleService.deleteService(id);
        return ResponseEntity.ok(ApiResponse.success("Service deleted", null));
    }
}
