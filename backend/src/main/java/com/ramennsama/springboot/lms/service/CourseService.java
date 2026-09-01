package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.dto.request.CourseRequest;
import com.ramennsama.springboot.lms.dto.response.CourseDetailResponse;
import com.ramennsama.springboot.lms.dto.response.CourseResponse;
import com.ramennsama.springboot.lms.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    CourseResponse updateCourse(Long courseId, CourseRequest request);
    void deleteCourse(Long courseId);
    CourseDetailResponse getCourseDetail(Long id);
    Page<CourseResponse> getAllCourses(Pageable pageable);
    Page<CourseResponse> getMyCreatedCourses(Pageable pageable);
    Page<CourseResponse> getPendingCourses(Pageable pageable);
    CourseResponse changeCourseStatus(Long courseId, CourseStatus status);
    List<CourseResponse> getTop5NewestCourses();
}
