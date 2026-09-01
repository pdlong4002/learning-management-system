package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.dto.request.ReviewRequest;
import com.ramennsama.springboot.lms.dto.response.ReviewResponse;
import com.ramennsama.springboot.lms.entity.Course;
import com.ramennsama.springboot.lms.entity.Review;
import com.ramennsama.springboot.lms.entity.User;
import com.ramennsama.springboot.lms.exception.AppException;
import com.ramennsama.springboot.lms.exception.ErrorCode;
import com.ramennsama.springboot.lms.mapper.ReviewMapper;
import com.ramennsama.springboot.lms.repository.CourseRepository;
import com.ramennsama.springboot.lms.repository.EnrollmentRepository;
import com.ramennsama.springboot.lms.repository.ReviewRepository;
import com.ramennsama.springboot.lms.service.ReviewService;
import com.ramennsama.springboot.lms.utils.FindAuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewMapper reviewMapper;
    private final FindAuthenticatedUser findAuthenticatedUser;

    @Override
    @Transactional
    public ReviewResponse createReview(Long courseId, ReviewRequest request) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // Check if enrolled
        if (!enrollmentRepository.existsByUserIdAndCourseId(currentUser.getId(), courseId)) {
            throw new AppException(ErrorCode.NOT_ENROLLED);
        }

        // Check if already reviewed
        if (reviewRepository.existsByUserIdAndCourseId(currentUser.getId(), courseId)) {
            throw new AppException(ErrorCode.ALREADY_REVIEWED);
        }

        Review review = Review.builder()
                .user(currentUser)
                .course(course)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);
        updateCourseAverageRating(course);

        return reviewMapper.toReviewResponse(review);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);
        updateCourseAverageRating(review.getCourse());

        return reviewMapper.toReviewResponse(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        Course course = review.getCourse();
        reviewRepository.delete(review);
        reviewRepository.flush(); // ensure deletion happens before calculation
        
        updateCourseAverageRating(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getCourseReviews(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        return reviewRepository.findByCourseId(courseId).stream()
                .map(reviewMapper::toReviewResponse)
                .collect(Collectors.toList());
    }

    private void updateCourseAverageRating(Course course) {
        Double avgRating = reviewRepository.getAverageRatingByCourseId(course.getId());
        Integer totalReviews = reviewRepository.getTotalReviewsByCourseId(course.getId());
        
        course.setAverageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        course.setTotalReviews(totalReviews != null ? totalReviews : 0);
        
        courseRepository.save(course);
    }
}
