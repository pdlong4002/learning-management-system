package com.ramennsama.springboot.lms.repository;

import com.ramennsama.springboot.lms.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByCourseId(Long courseId);
    
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    
    Optional<Review> findByUserIdAndCourseId(Long userId, Long courseId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.id = :courseId")
    Double getAverageRatingByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.course.id = :courseId")
    Integer getTotalReviewsByCourseId(@Param("courseId") Long courseId);
}
