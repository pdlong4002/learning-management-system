package com.ramennsama.springboot.lms.mapper;

import com.ramennsama.springboot.lms.dto.response.EnrollmentResponse;
import com.ramennsama.springboot.lms.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CourseMapper.class})
public interface EnrollmentMapper {

    @Mapping(target = "student", source = "user")
    @Mapping(target = "enrolledAt", source = "createdAt")
    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);
}
