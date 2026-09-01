package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.dto.request.LessonRequest;
import com.ramennsama.springboot.lms.dto.response.LessonResponse;

public interface LessonService {
    LessonResponse createLesson(Long sectionId, LessonRequest request);
    LessonResponse updateLesson(Long lessonId, LessonRequest request);
    void deleteLesson(Long lessonId);
}
