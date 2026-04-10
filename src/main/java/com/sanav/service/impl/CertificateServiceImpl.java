package com.sanav.service.impl;

import com.sanav.entity.Certificate;
import com.sanav.exception.ResourceNotFoundException;
import com.sanav.repository.CertificateRepository;
import com.sanav.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateRepository repository;

    @Override
    public Certificate verifyCertificate(String certificateCode) {
        return repository.findByCertificateCode(certificateCode)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid certificate code: " + certificateCode));
    }

    @Override
    public Certificate issueCertificate(Certificate certificate) {
        if (certificate.getCertificateCode() == null || certificate.getCertificateCode().isEmpty()) {
            certificate.setCertificateCode("SANAV-CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        
        if (repository.existsByCertificateCode(certificate.getCertificateCode())) {
            throw new RuntimeException("Certificate code already exists: " + certificate.getCertificateCode());
        }
        
        if (certificate.getIssueDate() == null) {
            certificate.setIssueDate(LocalDateTime.now());
        }
        
        return repository.save(certificate);
    }
}
