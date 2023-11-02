package com.springboot.intro.service.impl;

import com.springboot.intro.dto.request.BookRequestDto;
import com.springboot.intro.dto.response.BookDtoWithoutCategoryIds;
import com.springboot.intro.dto.response.BookResponseDto;
import com.springboot.intro.exception.EntityNotFoundException;
import com.springboot.intro.mapper.BookMapper;
import com.springboot.intro.model.Book;
import com.springboot.intro.model.Category;
import com.springboot.intro.repository.BookRepository;
import com.springboot.intro.repository.CategoryRepository;
import com.springboot.intro.service.BookService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Override
    public BookResponseDto save(BookRequestDto bookRequestDto) {
        Book book = bookMapper.toModel(bookRequestDto);
        Category category = categoryRepository.findById(bookRequestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("There is no category by id "
                        + bookRequestDto.getCategoryId() + " Can't create book!"));
        book.setCategories(Set.of(category));
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookResponseDto getBookById(Long id) {
        return bookMapper.toDto(bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id " + id)));
    }

    @Override
    public List<BookResponseDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
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
            Book book = bookRepository.findById(id).get();
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

    @Override
    public List<BookDtoWithoutCategoryIds> getBooksByCategory(Long categoryId) {
        return bookRepository.findAllByCategoryId(categoryId).stream()
                .map(bookMapper::toDtoWithOutCategories)
                .toList();
    }
}
