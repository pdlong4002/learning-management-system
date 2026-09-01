package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.dto.request.ReviewRequest;
import com.ramennsama.springboot.lms.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(Long courseId, ReviewRequest request);
    
    ReviewResponse updateReview(Long reviewId, ReviewRequest request);
    
    void deleteReview(Long reviewId);
    
    List<ReviewResponse> getCourseReviews(Long courseId);
}
