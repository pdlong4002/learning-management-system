package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.dto.response.CourseProgressResponse;

public interface ProgressService {
    void markLessonAsCompleted(Long lessonId);
    CourseProgressResponse getCourseProgress(Long courseId);
}
