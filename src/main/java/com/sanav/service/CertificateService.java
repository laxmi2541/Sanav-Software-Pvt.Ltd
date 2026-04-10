package com.sanav.service;

import com.sanav.entity.Certificate;

public interface CertificateService {
    Certificate verifyCertificate(String certificateCode);
    Certificate issueCertificate(Certificate certificate);
}
