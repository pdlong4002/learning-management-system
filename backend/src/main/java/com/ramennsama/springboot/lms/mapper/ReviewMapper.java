package com.ramennsama.springboot.lms.mapper;

import com.ramennsama.springboot.lms.dto.response.ReviewResponse;
import com.ramennsama.springboot.lms.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ReviewMapper {

    @Mapping(target = "courseId", source = "course.id")
    ReviewResponse toReviewResponse(Review review);
}
