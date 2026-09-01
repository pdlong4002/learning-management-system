package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.dto.request.SectionRequest;
import com.ramennsama.springboot.lms.dto.response.SectionResponse;
import com.ramennsama.springboot.lms.entity.Course;
import com.ramennsama.springboot.lms.entity.Section;
import com.ramennsama.springboot.lms.entity.User;
import com.ramennsama.springboot.lms.enums.Role;
import com.ramennsama.springboot.lms.exception.AppException;
import com.ramennsama.springboot.lms.exception.ErrorCode;
import com.ramennsama.springboot.lms.mapper.SectionMapper;
import com.ramennsama.springboot.lms.repository.CourseRepository;
import com.ramennsama.springboot.lms.repository.SectionRepository;
import com.ramennsama.springboot.lms.service.SectionService;
import com.ramennsama.springboot.lms.utils.FindAuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final SectionMapper sectionMapper;
    private final FindAuthenticatedUser findAuthenticatedUser;

    @Override
    @Transactional
    public SectionResponse createSection(Long courseId, SectionRequest request) {
        Course course = getCourseAndCheckPermission(courseId);

        Section section = sectionMapper.toSection(request);
        section.setCourse(course);

        section = sectionRepository.save(section);
        return sectionMapper.toSectionResponse(section);
    }

    @Override
    @Transactional
    public SectionResponse updateSection(Long sectionId, SectionRequest request) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new AppException(ErrorCode.SECTION_NOT_FOUND));
        getCourseAndCheckPermission(section.getCourse().getId()); // Check permission

        sectionMapper.updateSectionFromRequest(request, section);
        section = sectionRepository.save(section);
        return sectionMapper.toSectionResponse(section);
    }

    @Override
    @Transactional
    public void deleteSection(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new AppException(ErrorCode.SECTION_NOT_FOUND));
        getCourseAndCheckPermission(section.getCourse().getId()); // Check permission

        sectionRepository.delete(section);
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
}
