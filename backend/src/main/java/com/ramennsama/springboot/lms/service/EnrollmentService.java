package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.dto.response.EnrollmentResponse;
import com.ramennsama.springboot.lms.entity.Course;
import com.ramennsama.springboot.lms.entity.User;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enrollCourse(Long courseId);
    List<EnrollmentResponse> getMyEnrollments();
    void enrollUserInCourse(User user, Course course);
}
