package com.ramennsama.springboot.lms.controller;

import com.ramennsama.springboot.lms.dto.response.ApiResponse;
import com.ramennsama.springboot.lms.dto.response.CourseProgressResponse;
import com.ramennsama.springboot.lms.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/{lessonId}/complete")
    public ResponseEntity<ApiResponse<Void>> markLessonAsCompleted(@PathVariable Long lessonId) {
        progressService.markLessonAsCompleted(lessonId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Lesson marked as completed")
                .build());
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<CourseProgressResponse>> getCourseProgress(@PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.<CourseProgressResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Course progress fetched successfully")
                .data(progressService.getCourseProgress(courseId))
                .build());
    }
}
