package com.springboot.intro.service.impl;

import com.springboot.intro.dto.request.CategoryRequestDto;
import com.springboot.intro.dto.response.CategoryResponseDto;
import com.springboot.intro.exception.EntityNotFoundException;
import com.springboot.intro.mapper.CategoryMapper;
import com.springboot.intro.model.Category;
import com.springboot.intro.repository.CategoryRepository;
import com.springboot.intro.service.CategoryService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponseDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        return categoryMapper.toDto(categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no such category by id " + id)));
    }

    @Override
    public CategoryResponseDto save(CategoryRequestDto requestDto) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toEntity(requestDto)));
    }

    @Override
    public CategoryResponseDto update(Long id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no such category by id " + id));
        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
