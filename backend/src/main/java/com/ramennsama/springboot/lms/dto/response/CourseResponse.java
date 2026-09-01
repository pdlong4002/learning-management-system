package com.ramennsama.springboot.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.ramennsama.springboot.lms.enums.CourseStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String thumbnailUrl;
    private Double averageRating;
    private Integer totalReviews;
    private CourseStatus status;
    private CategoryResponse category;
    private UserResponse instructor;
}
