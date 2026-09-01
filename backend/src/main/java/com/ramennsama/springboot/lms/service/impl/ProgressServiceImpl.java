package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.dto.response.CourseProgressResponse;
import com.ramennsama.springboot.lms.dto.response.LessonProgressResponse;
import com.ramennsama.springboot.lms.entity.Course;
import com.ramennsama.springboot.lms.entity.Lesson;
import com.ramennsama.springboot.lms.entity.User;
import com.ramennsama.springboot.lms.entity.UserLessonProgress;
import com.ramennsama.springboot.lms.exception.AppException;
import com.ramennsama.springboot.lms.exception.ErrorCode;
import com.ramennsama.springboot.lms.repository.CourseRepository;
import com.ramennsama.springboot.lms.repository.EnrollmentRepository;
import com.ramennsama.springboot.lms.repository.LessonRepository;
import com.ramennsama.springboot.lms.repository.UserLessonProgressRepository;
import com.ramennsama.springboot.lms.service.ProgressService;
import com.ramennsama.springboot.lms.utils.FindAuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final UserLessonProgressRepository userLessonProgressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;

    @Override
    @Transactional
    public void markLessonAsCompleted(Long lessonId) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        Long courseId = lesson.getSection().getCourse().getId();
        if (!enrollmentRepository.existsByUserIdAndCourseId(currentUser.getId(), courseId)) {
            throw new AppException(ErrorCode.NOT_ENROLLED);
        }

        Optional<UserLessonProgress> existingProgress = userLessonProgressRepository.findByUserIdAndLessonId(currentUser.getId(), lessonId);
        
        if (existingProgress.isPresent()) {
            UserLessonProgress progress = existingProgress.get();
            if (!progress.isCompleted()) {
                progress.setCompleted(true);
                userLessonProgressRepository.save(progress);
                updateCourseProgress(currentUser.getId(), courseId);
            }
        } else {
            UserLessonProgress progress = UserLessonProgress.builder()
                    .user(currentUser)
                    .lesson(lesson)
                    .isCompleted(true)
                    .build();
            userLessonProgressRepository.save(progress);
            updateCourseProgress(currentUser.getId(), courseId);
        }
    }

    private void updateCourseProgress(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return;

        List<UserLessonProgress> progresses = userLessonProgressRepository.findByUserIdAndLesson_Section_CourseId(userId, courseId);
        int totalLessons = course.getSections().stream()
                .mapToInt(section -> section.getLessons().size())
                .sum();
        int completedLessons = (int) progresses.stream().filter(UserLessonProgress::isCompleted).count();
        double progressPercentage = totalLessons == 0 ? 0 : Math.round((double) completedLessons / totalLessons * 1000.0) / 10.0;

        enrollmentRepository.findByUserIdAndCourseId(userId, courseId).ifPresent(enrollment -> {
            enrollment.setProgressPercent(BigDecimal.valueOf(progressPercentage));
            enrollmentRepository.save(enrollment);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public CourseProgressResponse getCourseProgress(Long courseId) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!enrollmentRepository.existsByUserIdAndCourseId(currentUser.getId(), courseId)) {
            throw new AppException(ErrorCode.NOT_ENROLLED);
        }

        List<UserLessonProgress> progresses = userLessonProgressRepository.findByUserIdAndLesson_Section_CourseId(currentUser.getId(), courseId);
        
        List<LessonProgressResponse> lessonResponses = progresses.stream()
                .map(p -> LessonProgressResponse.builder()
                        .lessonId(p.getLesson().getId())
                        .lessonTitle(p.getLesson().getTitle())
                        .isCompleted(p.isCompleted())
                        .completedAt(p.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        int totalLessons = course.getSections().stream()
                .mapToInt(section -> section.getLessons().size())
                .sum();
        int completedLessons = (int) progresses.stream().filter(UserLessonProgress::isCompleted).count();
        double progressPercentage = totalLessons == 0 ? 0 : Math.round((double) completedLessons / totalLessons * 1000.0) / 10.0;

        return CourseProgressResponse.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .progressPercentage(progressPercentage)
                .lessonProgresses(lessonResponses)
                .build();
    }
}
