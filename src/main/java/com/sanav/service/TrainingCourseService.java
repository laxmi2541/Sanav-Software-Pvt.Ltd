package com.sanav.service;

import com.sanav.entity.TrainingCourse;
import java.util.List;

public interface TrainingCourseService {
    List<TrainingCourse> getAllCourses();
    TrainingCourse getCourseById(Long id);
    TrainingCourse createCourse(TrainingCourse course);
    TrainingCourse updateCourse(Long id, TrainingCourse course);
    void deleteCourse(Long id);
}
