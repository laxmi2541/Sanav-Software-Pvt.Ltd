package com.sanav.repository;

import com.sanav.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByCertificateCode(String certificateCode);
    boolean existsByCertificateCode(String certificateCode);
}
