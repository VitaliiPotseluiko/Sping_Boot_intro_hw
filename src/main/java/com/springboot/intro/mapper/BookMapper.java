package com.springboot.intro.mapper;

import com.springboot.intro.config.MapperConfig;
import com.springboot.intro.dto.request.BookRequestDto;
import com.springboot.intro.dto.response.BookResponseDto;
import com.springboot.intro.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookResponseDto toDto(Book book);

    Book toModel(BookRequestDto requestDto);
}
