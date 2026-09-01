package com.ramennsama.springboot.lms.controller;

import com.ramennsama.springboot.lms.dto.request.CourseRequest;
import com.ramennsama.springboot.lms.dto.response.ApiResponse;
import com.ramennsama.springboot.lms.dto.response.CourseDetailResponse;
import com.ramennsama.springboot.lms.dto.response.CourseResponse;
import com.ramennsama.springboot.lms.enums.CourseStatus;
import com.ramennsama.springboot.lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CourseResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Course created successfully")
                .data(courseService.createCourse(request))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id, 
            @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Course updated successfully")
                .data(courseService.updateCourse(id, request))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Course deleted successfully")
                .build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CourseResponse>> changeCourseStatus(
            @PathVariable Long id, 
            @RequestParam CourseStatus status) {
        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Course status updated successfully")
                .data(courseService.changeCourseStatus(id, status))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> getCourseDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<CourseDetailResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Course details fetched successfully")
                .data(courseService.getCourseDetail(id))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getAllCourses(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<Page<CourseResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Courses fetched successfully")
                .data(courseService.getAllCourses(pageable))
                .build());
    }

    @GetMapping("/instructor/my-courses")
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getMyCreatedCourses(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<Page<CourseResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Instructor courses fetched successfully")
                .data(courseService.getMyCreatedCourses(pageable))
                .build());
    }

    @GetMapping("/admin/pending-courses")
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getPendingCourses(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.<Page<CourseResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Pending courses fetched successfully")
                .data(courseService.getPendingCourses(pageable))
                .build());
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getTop5NewestCourses() {
        return ResponseEntity.ok(ApiResponse.<List<CourseResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Latest courses fetched successfully")
                .data(courseService.getTop5NewestCourses())
                .build());
    }

}
