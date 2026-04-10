package com.sanav.controller;

import com.sanav.entity.DemoRequest;
import com.sanav.response.ApiResponse;
import com.sanav.service.DemoRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DemoRequestController {

    @Autowired
    private DemoRequestService service;

    @PostMapping("/demo-request")
    public ResponseEntity<ApiResponse> createDemoRequest(@RequestBody DemoRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Demo request submitted", service.saveDemoRequest(request)));
    }

    @GetMapping("/admin/demo-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllDemoRequests() {
        return ResponseEntity.ok(ApiResponse.success("Demo requests retrieved", service.getAllDemoRequests()));
    }
}
