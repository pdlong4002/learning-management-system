package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.dto.request.CourseRequest;
import com.ramennsama.springboot.lms.dto.response.CourseDetailResponse;
import com.ramennsama.springboot.lms.dto.response.CourseResponse;
import com.ramennsama.springboot.lms.entity.Category;
import com.ramennsama.springboot.lms.entity.Course;
import com.ramennsama.springboot.lms.entity.User;
import com.ramennsama.springboot.lms.enums.CourseStatus;
import com.ramennsama.springboot.lms.enums.Role;
import com.ramennsama.springboot.lms.exception.AppException;
import com.ramennsama.springboot.lms.exception.ErrorCode;
import com.ramennsama.springboot.lms.mapper.CourseMapper;
import com.ramennsama.springboot.lms.repository.CategoryRepository;
import com.ramennsama.springboot.lms.repository.CourseRepository;
import com.ramennsama.springboot.lms.service.CourseService;
import com.ramennsama.springboot.lms.utils.FindAuthenticatedUser;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final CourseMapper courseMapper;
    private final FindAuthenticatedUser findAuthenticatedUser;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        User instructor = findAuthenticatedUser.getAuthenticatedUser();
        if (instructor.getRole() != Role.INSTRUCTOR && instructor.getRole() != Role.ADMIN) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Course course = courseMapper.toCourse(request);
        course.setInstructor(instructor);
        course.setCategory(category);
        course.setStatus(CourseStatus.DRAFT); // Default status

        course = courseRepository.save(course);
        return courseMapper.toCourseResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long courseId, CourseRequest request) {
        Course course = getCourseAndCheckPermission(courseId);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        courseMapper.updateCourseFromRequest(request, course);
        course.setCategory(category);

        course = courseRepository.save(course);
        return courseMapper.toCourseResponse(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = getCourseAndCheckPermission(courseId);
        courseRepository.delete(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            try {
                User currentUser = findAuthenticatedUser.getAuthenticatedUser();
                if (currentUser.getRole() != Role.ADMIN && !course.getInstructor().getId().equals(currentUser.getId())) {
                    throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
                }
            } catch (Exception e) {
                // If not logged in or invalid token, deny access
                throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
            }
        }
        
        return courseMapper.toCourseDetailResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAllCourses(Pageable pageable) {
        return courseRepository.findByStatus(CourseStatus.PUBLISHED, pageable)
                .map(courseMapper::toCourseResponse);
    }

    @Override
    @Transactional
    public CourseResponse changeCourseStatus(Long courseId, CourseStatus status) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        User currentUser = findAuthenticatedUser.getAuthenticatedUser();
        // Instructor can only change DRAFT -> PENDING
        if (currentUser.getRole() == Role.INSTRUCTOR) {
            if (!course.getInstructor().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
            }
            if (status != CourseStatus.PENDING) {
                throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
            }
        } else if (currentUser.getRole() != Role.ADMIN) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        course.setStatus(status);
        course = courseRepository.save(course);
        return courseMapper.toCourseResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getMyCreatedCourses(Pageable pageable) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();
        return courseRepository.findByInstructorId(currentUser.getId(), pageable)
                .map(courseMapper::toCourseResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getPendingCourses(Pageable pageable) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }
        return courseRepository.findByStatus(CourseStatus.PENDING, pageable)
                .map(courseMapper::toCourseResponse);
    }

    private Course getCourseAndCheckPermission(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        User currentUser = findAuthenticatedUser.getAuthenticatedUser();
        if (currentUser.getRole() != Role.ADMIN && !course.getInstructor().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }
        return course;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getTop5NewestCourses() {
        return courseRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(courseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }
}
