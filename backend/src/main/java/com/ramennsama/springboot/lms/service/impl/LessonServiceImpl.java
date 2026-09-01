package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.dto.request.LessonRequest;
import com.ramennsama.springboot.lms.dto.response.LessonResponse;
import com.ramennsama.springboot.lms.entity.Course;
import com.ramennsama.springboot.lms.entity.Lesson;
import com.ramennsama.springboot.lms.entity.Section;
import com.ramennsama.springboot.lms.entity.User;
import com.ramennsama.springboot.lms.enums.Role;
import com.ramennsama.springboot.lms.exception.AppException;
import com.ramennsama.springboot.lms.exception.ErrorCode;
import com.ramennsama.springboot.lms.mapper.LessonMapper;
import com.ramennsama.springboot.lms.repository.LessonRepository;
import com.ramennsama.springboot.lms.repository.SectionRepository;
import com.ramennsama.springboot.lms.service.LessonService;
import com.ramennsama.springboot.lms.utils.FindAuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;
    private final LessonMapper lessonMapper;
    private final FindAuthenticatedUser findAuthenticatedUser;

    @Override
    @Transactional
    public LessonResponse createLesson(Long sectionId, LessonRequest request) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new AppException(ErrorCode.SECTION_NOT_FOUND));
        checkCoursePermission(section.getCourse());

        Lesson lesson = lessonMapper.toLesson(request);
        lesson.setSection(section);

        lesson = lessonRepository.save(lesson);
        return lessonMapper.toLessonResponse(lesson);
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(Long lessonId, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        checkCoursePermission(lesson.getSection().getCourse());

        lessonMapper.updateLessonFromRequest(request, lesson);
        lesson = lessonRepository.save(lesson);
        return lessonMapper.toLessonResponse(lesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        checkCoursePermission(lesson.getSection().getCourse());

        lessonRepository.delete(lesson);
    }

    private void checkCoursePermission(Course course) {
        User currentUser = findAuthenticatedUser.getAuthenticatedUser();
        if (currentUser.getRole() != Role.ADMIN && !course.getInstructor().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }
    }
}
