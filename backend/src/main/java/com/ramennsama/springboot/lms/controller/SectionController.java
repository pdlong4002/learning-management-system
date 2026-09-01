package com.ramennsama.springboot.lms.controller;

import com.ramennsama.springboot.lms.dto.request.SectionRequest;
import com.ramennsama.springboot.lms.dto.response.ApiResponse;
import com.ramennsama.springboot.lms.dto.response.SectionResponse;
import com.ramennsama.springboot.lms.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping("/courses/{courseId}/sections")
    public ResponseEntity<ApiResponse<SectionResponse>> createSection(
            @PathVariable Long courseId,
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<SectionResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Section created successfully")
                .data(sectionService.createSection(courseId, request))
                .build());
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<ApiResponse<SectionResponse>> updateSection(
            @PathVariable Long id,
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(ApiResponse.<SectionResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Section updated successfully")
                .data(sectionService.updateSection(id, request))
                .build());
    }

    @DeleteMapping("/sections/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Section deleted successfully")
                .build());
    }
}
