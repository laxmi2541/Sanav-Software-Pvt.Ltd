package com.sanav.service.impl;

import com.sanav.entity.TrainingCourse;
import com.sanav.exception.ResourceNotFoundException;
import com.sanav.repository.TrainingCourseRepository;
import com.sanav.service.TrainingCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingCourseServiceImpl implements TrainingCourseService {

    @Autowired
    private TrainingCourseRepository repository;

    @Override
    public List<TrainingCourse> getAllCourses() {
        return repository.findAll();
    }

    @Override
    public TrainingCourse getCourseById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    @Override
    public TrainingCourse createCourse(TrainingCourse course) {
        return repository.save(course);
    }

    @Override
    public TrainingCourse updateCourse(Long id, TrainingCourse course) {
        TrainingCourse existing = getCourseById(id);
        existing.setTitle(course.getTitle());
        existing.setDescription(course.getDescription());
        existing.setDuration(course.getDuration());
        existing.setFees(course.getFees());
        return repository.save(existing);
    }

    @Override
    public void deleteCourse(Long id) {
        TrainingCourse existing = getCourseById(id);
        repository.delete(existing);
    }
}
