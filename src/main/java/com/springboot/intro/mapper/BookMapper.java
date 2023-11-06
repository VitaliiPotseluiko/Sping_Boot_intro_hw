package com.springboot.intro.mapper;

import com.springboot.intro.config.MapperConfig;
import com.springboot.intro.dto.request.BookRequestDto;
import com.springboot.intro.dto.response.BookDtoWithoutCategoryIds;
import com.springboot.intro.dto.response.BookResponseDto;
import com.springboot.intro.model.Book;
import com.springboot.intro.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookResponseDto toDto(Book book);

    Book toModel(BookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithOutCategories(Book book);

    @AfterMapping default void setCategories(@MappingTarget BookResponseDto responseDto, Book book) {
        Set<Long> ids = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        responseDto.setCategoryIds(ids);
    }
}
