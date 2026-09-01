package com.ramennsama.springboot.lms.controller;

import com.ramennsama.springboot.lms.dto.response.ApiResponse;
import com.ramennsama.springboot.lms.dto.response.EnrollmentResponse;
import com.ramennsama.springboot.lms.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/{courseId}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(@PathVariable Long courseId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<EnrollmentResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Enrolled successfully")
                .data(enrollmentService.enrollCourse(courseId))
                .build());
    }

    @GetMapping("/my-courses")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments() {
        return ResponseEntity.ok(ApiResponse.<List<EnrollmentResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("My enrollments fetched successfully")
                .data(enrollmentService.getMyEnrollments())
                .build());
    }
}
