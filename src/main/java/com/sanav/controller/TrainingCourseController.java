package com.sanav.controller;

import com.sanav.entity.TrainingCourse;
import com.sanav.response.ApiResponse;
import com.sanav.service.TrainingCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TrainingCourseController {

    @Autowired
    private TrainingCourseService service;

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse> getAllCourses() {
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved", service.getAllCourses()));
    }
}
