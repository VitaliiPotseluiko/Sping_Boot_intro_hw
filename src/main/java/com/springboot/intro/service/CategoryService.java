package com.springboot.intro.service;

import com.springboot.intro.dto.request.CategoryRequestDto;
import com.springboot.intro.dto.response.BookDtoWithoutCategoryIds;
import com.springboot.intro.dto.response.CategoryResponseDto;
import com.springboot.intro.model.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> findAll();

    CategoryResponseDto getById(Long id);

    CategoryResponseDto save(CategoryRequestDto requestDto);

    CategoryResponseDto update(Long id, CategoryRequestDto requestDto);

    void deleteById(Long id);
}
