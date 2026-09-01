package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.dto.response.EnrollmentResponse;
import com.ramennsama.springboot.lms.entity.Course;
import com.ramennsama.springboot.lms.entity.Enrollment;
import com.ramennsama.springboot.lms.entity.User;
import com.ramennsama.springboot.lms.exception.AppException;
import com.ramennsama.springboot.lms.exception.ErrorCode;
import com.ramennsama.springboot.lms.mapper.EnrollmentMapper;
import com.ramennsama.springboot.lms.repository.CourseRepository;
import com.ramennsama.springboot.lms.repository.EnrollmentRepository;
import com.ramennsama.springboot.lms.service.EnrollmentService;
import com.ramennsama.springboot.lms.utils.FindAuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final FindAuthenticatedUser findAuthenticatedUser;

    @Override
    @Transactional
    public EnrollmentResponse enrollCourse(Long courseId) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (enrollmentRepository.existsByUserIdAndCourseId(currentUser.getId(), courseId)) {
            throw new AppException(ErrorCode.ALREADY_ENROLLED);
        }

        Enrollment enrollment = Enrollment.builder()
                .user(currentUser)
                .course(course)
                .progressPercent(BigDecimal.ZERO)
                .build();

        enrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toEnrollmentResponse(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments() {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();
        return enrollmentRepository.findByUserId(currentUser.getId()).stream()
                .map(enrollmentMapper::toEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void enrollUserInCourse(User user, Course course) {
        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            return;
        }
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .progressPercent(java.math.BigDecimal.ZERO)
                .build();
        enrollmentRepository.save(enrollment);
    }
}
