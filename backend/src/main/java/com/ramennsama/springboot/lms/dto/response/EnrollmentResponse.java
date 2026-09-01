package com.ramennsama.springboot.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private UserResponse student;
    private CourseResponse course;
    private LocalDateTime enrolledAt;
    private BigDecimal progressPercent;
}
