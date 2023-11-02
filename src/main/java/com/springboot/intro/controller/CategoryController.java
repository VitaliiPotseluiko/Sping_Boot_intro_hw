package com.springboot.intro.controller;

import com.springboot.intro.dto.request.CategoryRequestDto;
import com.springboot.intro.dto.response.BookDtoWithoutCategoryIds;
import com.springboot.intro.dto.response.CategoryResponseDto;
import com.springboot.intro.service.BookService;
import com.springboot.intro.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management", description = "Endpoints for managing categories")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Create category", description = "create a new category")
    public CategoryResponseDto createCategory(@Parameter(
            description = "Object for creating a new category",
            required = true,
            content = @Content(schema = @Schema(implementation = CategoryRequestDto.class))
    ) @RequestBody @Valid CategoryRequestDto requestDto) {
        return categoryService.save(requestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get all categories", description = "Get all available categories")
    public List<CategoryResponseDto> getAll() {
        return categoryService.findAll();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get category by id", description = "Get category by id")
    public CategoryResponseDto getCategoryById(@Parameter(
            description = "id of certain category",
            name = "id",
            required = true,
            example = "1"
    ) @PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update category by id", description = "Update category by id")
    public CategoryResponseDto updateCategory(@Parameter(
            description = "id of certain category",
            name = "id",
            required = true,
            example = "1"
    ) @PathVariable Long id, @Parameter(
            description = "Object for updating a new category",
            required = true,
            content = @Content(schema = @Schema(implementation = CategoryRequestDto.class))
    ) @RequestBody @Valid CategoryRequestDto requestDto) {
        return categoryService.update(id, requestDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by id", description = "Delete category by id")
    public void deleteCategory(@Parameter(
            description = "id of certain category",
            name = "id",
            required = true,
            example = "1"
    ) @PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}/books")
    public List<BookDtoWithoutCategoryIds> getBooksByCategory(@PathVariable Long id) {
        return bookService.getBooksByCategory(id);
    }
}
