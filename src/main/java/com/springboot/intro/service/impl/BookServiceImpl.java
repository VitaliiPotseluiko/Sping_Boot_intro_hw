package com.springboot.intro.service.impl;

import com.springboot.intro.dto.request.BookRequestDto;
import com.springboot.intro.dto.response.BookResponseDto;
import com.springboot.intro.exception.EntityNotFoundException;
import com.springboot.intro.mapper.BookMapper;
import com.springboot.intro.model.Book;
import com.springboot.intro.repository.BookRepository;
import com.springboot.intro.service.BookService;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookResponseDto save(BookRequestDto bookRequestDto) {
        return bookMapper.toDto(bookRepository.save(bookMapper.toModel(bookRequestDto)));
    }

    @Override
    public BookResponseDto getBookById(Long id) {
        return bookMapper.toDto(bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id " + id)));
    }

    @Override
    public List<BookResponseDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookResponseDto update(Long id, BookRequestDto bookRequestDto) {
        if (bookRepository.existsById(id)) {
            Book book = bookRepository.getReferenceById(id);
            book.setPrice(bookRequestDto.getPrice());
            book.setTitle(bookRequestDto.getTitle());
            book.setAuthor(bookRequestDto.getAuthor());
            book.setDescription(bookRequestDto.getDescription());
            book.setCoverImage(bookRequestDto.getCoverImage());
            return bookMapper.toDto(bookRepository.save(book));
        }
        throw new EntityNotFoundException("Can't update book by id "
                + id + ". There is no such entity");
    }
}
