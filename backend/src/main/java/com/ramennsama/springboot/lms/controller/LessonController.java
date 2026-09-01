package com.ramennsama.springboot.lms.controller;

import com.ramennsama.springboot.lms.dto.request.LessonRequest;
import com.ramennsama.springboot.lms.dto.response.ApiResponse;
import com.ramennsama.springboot.lms.dto.response.LessonResponse;
import com.ramennsama.springboot.lms.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/sections/{sectionId}/lessons")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @PathVariable Long sectionId,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<LessonResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Lesson created successfully")
                .data(lessonService.createLesson(sectionId, request))
                .build());
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(ApiResponse.<LessonResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Lesson updated successfully")
                .data(lessonService.updateLesson(id, request))
                .build());
    }

    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Lesson deleted successfully")
                .build());
    }
}
