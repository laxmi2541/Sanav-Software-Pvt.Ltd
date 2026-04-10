package com.sanav.controller;

import com.sanav.entity.StudentRegistration;
import com.sanav.response.ApiResponse;
import com.sanav.service.StudentRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training")
public class StudentRegistrationController {

    @Autowired
    private StudentRegistrationService service;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody StudentRegistration registration) {
        return ResponseEntity.ok(ApiResponse.success("Student registered successfully", service.registerStudent(registration)));
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<ApiResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Student retrieved", service.getStudentById(id)));
    }

    @PostMapping("/verify-registration")
    public ResponseEntity<ApiResponse> verifyRegistration(@RequestBody java.util.Map<String, String> request) {
        String registrationNo = request.get("registrationNo");
        return ResponseEntity.ok(ApiResponse.success("Registration verified", service.verifyRegistration(registrationNo)));
    }
}
