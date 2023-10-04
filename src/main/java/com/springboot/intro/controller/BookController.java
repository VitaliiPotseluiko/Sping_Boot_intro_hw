package com.springboot.intro.controller;

import com.springboot.intro.dto.request.BookRequestDto;
import com.springboot.intro.dto.response.BookResponseDto;
import com.springboot.intro.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books", description = "Get all available books")
    public List<BookResponseDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @PostMapping
    @Operation(summary = "Create a new book", description = "Create a new book")
    public BookResponseDto save(@RequestBody @Valid BookRequestDto bookRequestDto) {
        return bookService.save(bookRequestDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by id", description = "Get book by id")
    public BookResponseDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book by id", description = "Delete book by id")
    public void deleteById(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update book by id", description = "Update book by id")
    public BookResponseDto update(@PathVariable Long id,
                                  @RequestBody @Valid BookRequestDto requestDto) {
        return bookService.update(id, requestDto);
    }
}
