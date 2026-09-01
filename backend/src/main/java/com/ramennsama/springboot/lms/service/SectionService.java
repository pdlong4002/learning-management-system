package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.dto.request.SectionRequest;
import com.ramennsama.springboot.lms.dto.response.SectionResponse;

public interface SectionService {
    SectionResponse createSection(Long courseId, SectionRequest request);
    SectionResponse updateSection(Long sectionId, SectionRequest request);
    void deleteSection(Long sectionId);
}
