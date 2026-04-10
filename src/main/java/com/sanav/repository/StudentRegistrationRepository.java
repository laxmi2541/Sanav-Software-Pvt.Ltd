package com.sanav.repository;

import com.sanav.entity.StudentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRegistrationRepository extends JpaRepository<StudentRegistration, Long> {
    Optional<StudentRegistration> findByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumber(String registrationNumber);
}
