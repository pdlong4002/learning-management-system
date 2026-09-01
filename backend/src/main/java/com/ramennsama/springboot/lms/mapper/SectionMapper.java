package com.ramennsama.springboot.lms.mapper;

import com.ramennsama.springboot.lms.dto.request.SectionRequest;
import com.ramennsama.springboot.lms.dto.response.SectionResponse;
import com.ramennsama.springboot.lms.entity.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {LessonMapper.class})
public interface SectionMapper {
    SectionResponse toSectionResponse(Section section);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    Section toSection(SectionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateSectionFromRequest(SectionRequest request, @MappingTarget Section section);
}
