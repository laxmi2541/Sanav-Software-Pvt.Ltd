package com.sanav.controller;

import com.sanav.entity.Certificate;
import com.sanav.response.ApiResponse;
import com.sanav.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training")
public class CertificateController {

    @Autowired
    private CertificateService service;

    @PostMapping("/verify-certificate")
    public ResponseEntity<ApiResponse> verifyCertificate(@RequestBody java.util.Map<String, String> request) {
        String certificateCode = request.get("certificateNo");
        return ResponseEntity.ok(ApiResponse.success("Certificate verified", service.verifyCertificate(certificateCode)));
    }
}
