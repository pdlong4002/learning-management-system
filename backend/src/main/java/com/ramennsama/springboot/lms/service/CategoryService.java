package com.ramennsama.springboot.lms.service;

import com.ramennsama.springboot.lms.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
}
