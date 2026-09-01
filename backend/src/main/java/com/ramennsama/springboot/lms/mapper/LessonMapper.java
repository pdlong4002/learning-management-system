package com.ramennsama.springboot.lms.mapper;

import com.ramennsama.springboot.lms.dto.request.LessonRequest;
import com.ramennsama.springboot.lms.dto.response.LessonResponse;
import com.ramennsama.springboot.lms.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    LessonResponse toLessonResponse(Lesson lesson);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "isFreePreview", source = "freePreview")
    Lesson toLesson(LessonRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateLessonFromRequest(LessonRequest request, @MappingTarget Lesson lesson);
}
