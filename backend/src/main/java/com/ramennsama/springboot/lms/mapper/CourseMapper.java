package com.ramennsama.springboot.lms.mapper;

import com.ramennsama.springboot.lms.dto.request.CourseRequest;
import com.ramennsama.springboot.lms.dto.response.CourseDetailResponse;
import com.ramennsama.springboot.lms.dto.response.CourseResponse;
import com.ramennsama.springboot.lms.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class, SectionMapper.class})
public interface CourseMapper {

    CourseResponse toCourseResponse(Course course);

    CourseDetailResponse toCourseDetailResponse(Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "category", ignore = true) // Will set manually from DB
    @Mapping(target = "sections", ignore = true)
    Course toCourse(CourseRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "category", ignore = true) // Will set manually from DB
    @Mapping(target = "sections", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateCourseFromRequest(CourseRequest request, @MappingTarget Course course);
}
