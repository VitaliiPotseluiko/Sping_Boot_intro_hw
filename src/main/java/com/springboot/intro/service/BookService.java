package com.springboot.intro.service;

import com.springboot.intro.dto.request.BookRequestDto;
import com.springboot.intro.dto.response.BookResponseDto;
import java.util.List;

public interface BookService {
    BookResponseDto save(BookRequestDto bookRequestDto);

    BookResponseDto getBookById(Long id);

    List<BookResponseDto> findAll();
}
