package com.sanav.service;

import com.sanav.entity.StudentRegistration;

public interface StudentRegistrationService {
    StudentRegistration registerStudent(StudentRegistration registration);
    StudentRegistration getStudentById(Long id);
    StudentRegistration verifyRegistration(String registrationNo);
}
