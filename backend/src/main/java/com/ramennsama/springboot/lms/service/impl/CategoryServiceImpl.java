package com.ramennsama.springboot.lms.service.impl;

import com.ramennsama.springboot.lms.dto.response.CategoryResponse;
import com.ramennsama.springboot.lms.mapper.CategoryMapper;
import com.ramennsama.springboot.lms.repository.CategoryRepository;
import com.ramennsama.springboot.lms.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }
}
