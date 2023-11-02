package com.springboot.intro.service;

import com.springboot.intro.dto.request.BookRequestDto;
import com.springboot.intro.dto.response.BookDtoWithoutCategoryIds;
import com.springboot.intro.dto.response.BookResponseDto;
import com.springboot.intro.model.Book;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookResponseDto save(BookRequestDto bookRequestDto);

    BookResponseDto getBookById(Long id);

    List<BookResponseDto> findAll(Pageable pageable);

    void deleteById(Long id);

    BookResponseDto update(Long id, BookRequestDto bookRequestDto);

    List<BookDtoWithoutCategoryIds> getBooksByCategory(Long categoryId);

}
