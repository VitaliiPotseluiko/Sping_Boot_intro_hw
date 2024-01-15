package com.springboot.intro.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.springboot.intro.dto.request.CategoryRequestDto;
import com.springboot.intro.dto.response.CategoryResponseDto;
import com.springboot.intro.exception.EntityNotFoundException;
import com.springboot.intro.mapper.CategoryMapper;
import com.springboot.intro.model.Category;
import com.springboot.intro.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private static Category category;
    private static CategoryResponseDto responseDto;

    @BeforeAll
    public static void setUp() {
        responseDto = new CategoryResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Fiction");
        responseDto.setDescription("Fiction books");

        category = new Category();
        category.setId(1L);
        category.setName("Fiction");
        category.setDescription("Fiction books");
    }

    @Test
    @DisplayName("""
            get category where its id = 1
            """)
    public void getById_GetCategoryWithIdOne_ReturnCategoryWithIdEqualsOne() {
        when(categoryRepository.findById(responseDto.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(responseDto);

        CategoryResponseDto actual = categoryService.getById(1L);

        assertEquals(1L, actual.getId());
        assertEquals("Fiction", actual.getName());
        assertEquals("Fiction books", actual.getDescription());
    }

    @Test
    @DisplayName("""
            get category with not existing id
            """)
    public void getById_GetCategoryWithNotExistingId_ReturnEntityNotFoundException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception actualException = assertThrows(
          EntityNotFoundException.class, () -> categoryService.getById(100L)
        );

        assertEquals("EntityNotFoundException", actualException.getClass().getSimpleName());
        assertEquals("There is no such category by id 100", actualException.getMessage());
    }

    @Test
    @DisplayName("""
            verify save() method works successfully
            """)
    public void save_SaveCategory_ReturnsSavedCategory() {
        when(categoryMapper.toEntity(any())).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(responseDto);

        CategoryResponseDto actual = categoryService.save(new CategoryRequestDto());

        assertEquals(1, actual.getId());
        assertEquals("Fiction", actual.getName());
        assertEquals("Fiction books", actual.getDescription());
    }

    @Test
    @DisplayName("""
            update category by not existing id
            """)
    public void update_UpdateCategoryWithNotExistingId_ReturnsEntityNotFoundException() {
        when(categoryRepository.findById(10L)).thenReturn(Optional.empty());

        Exception actualException = assertThrows(
                EntityNotFoundException.class, () -> categoryService.update(10L, new CategoryRequestDto())
        );

        assertEquals("EntityNotFoundException", actualException.getClass().getSimpleName());
        assertEquals("There is no such category by id 10", actualException.getMessage());
    }

    @Test
    @DisplayName("""
            update category id = 1
            """)
    public void update_UpdateCategoryByIdEqualsOne_ReturnsUpdatedCategory() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Science");
        requestDto.setDescription("Science books");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName(requestDto.getName());
        updatedCategory.setDescription(requestDto.getDescription());

        CategoryResponseDto updatedResponseDto = new CategoryResponseDto();
        updatedResponseDto.setId(updatedCategory.getId());
        updatedResponseDto.setName(updatedCategory.getName());
        updatedResponseDto.setDescription(updatedCategory.getDescription());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(updatedResponseDto);
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);

        CategoryResponseDto actual = categoryService.update(1L, requestDto);

        assertEquals(1L, actual.getId());
        assertEquals("Science", actual.getName());
        assertEquals("Science books", actual.getDescription());
    }

    @Test
    @DisplayName("""
            verify findAll() method works
            """)
    public void findAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(responseDto);

        List<CategoryResponseDto> actual = categoryService.findAll(pageable);

        assertEquals(1, actual.size());
        assertEquals(responseDto, actual.get(0));
    }
}