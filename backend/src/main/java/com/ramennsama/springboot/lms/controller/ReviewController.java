package com.ramennsama.springboot.lms.controller;

import com.ramennsama.springboot.lms.dto.request.ReviewRequest;
import com.ramennsama.springboot.lms.dto.response.ApiResponse;
import com.ramennsama.springboot.lms.dto.response.ReviewResponse;
import com.ramennsama.springboot.lms.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/courses/{courseId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long courseId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ReviewResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Review created successfully")
                .data(reviewService.createReview(courseId, request))
                .build());
    }

    @GetMapping("/courses/{courseId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getCourseReviews(@PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.<List<ReviewResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Reviews fetched successfully")
                .data(reviewService.getCourseReviews(courseId))
                .build());
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.<ReviewResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Review updated successfully")
                .data(reviewService.updateReview(reviewId, request))
                .build());
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Review deleted successfully")
                .build());
    }
}
