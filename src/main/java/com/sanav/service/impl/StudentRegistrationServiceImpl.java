package com.sanav.service.impl;

import com.sanav.entity.StudentRegistration;
import com.sanav.exception.ResourceNotFoundException;
import com.sanav.repository.StudentRegistrationRepository;
import com.sanav.service.StudentRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class StudentRegistrationServiceImpl implements StudentRegistrationService {

    @Autowired
    private StudentRegistrationRepository repository;

    @Override
    public StudentRegistration registerStudent(StudentRegistration registration) {
        // Generate a unique registration number if not provided
        if (registration.getRegistrationNumber() == null || registration.getRegistrationNumber().isEmpty()) {
            registration.setRegistrationNumber("SANAV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        
        if (repository.existsByRegistrationNumber(registration.getRegistrationNumber())) {
            throw new RuntimeException("Registration number already exists: " + registration.getRegistrationNumber());
        }
        
        if (registration.getJoiningDate() == null) {
            registration.setJoiningDate(LocalDateTime.now());
        }
        
        return repository.save(registration);
    }

    @Override
    public StudentRegistration getStudentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Override
    public StudentRegistration verifyRegistration(String registrationNo) {
        return repository.findByRegistrationNumber(registrationNo)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid registration number: " + registrationNo));
    }
}
