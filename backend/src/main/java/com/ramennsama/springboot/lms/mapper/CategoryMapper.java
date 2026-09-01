package com.ramennsama.springboot.lms.mapper;

import com.ramennsama.springboot.lms.dto.response.CategoryResponse;
import com.ramennsama.springboot.lms.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
}
