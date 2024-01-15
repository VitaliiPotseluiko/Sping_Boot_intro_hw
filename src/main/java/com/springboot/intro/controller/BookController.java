package com.springboot.intro.controller;

import com.springboot.intro.dto.request.BookRequestDto;
import com.springboot.intro.dto.request.BookSearchParametersDto;
import com.springboot.intro.dto.response.BookResponseDto;
import com.springboot.intro.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get all books", description = "Get all available books")
    public List<BookResponseDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new book", description = "Create a new book")
    public BookResponseDto save(@Parameter(
            description = "Object for creating a new book",
            required = true,
            content = @Content(schema = @Schema(implementation = BookRequestDto.class))
    ) @RequestBody @Valid BookRequestDto bookRequestDto) {
        return bookService.save(bookRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get book by id", description = "Get book by id")
    public BookResponseDto getBookById(@Parameter(
            description = "id of certain book",
            name = "id",
            required = true,
            example = "1"
    ) @PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book by id", description = "Delete book by id")
    public void deleteById(@Parameter(
            description = "id of certain book",
            name = "id",
            required = true,
            example = "1"
    ) @PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update book by id", description = "Update book by id")
    public BookResponseDto update(@Parameter(
            description = "id of certain book",
            name = "id",
            required = true,
            example = "1"
    ) @PathVariable Long id, @Parameter(
            description = "Object for updating a new book",
            required = true,
            content = @Content(schema = @Schema(implementation = BookRequestDto.class))
    ) @RequestBody @Valid BookRequestDto requestDto) {
        return bookService.update(id, requestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search book by specific criteria")
    public List<BookResponseDto> searchBooks(@Parameter(
            description = "Object that includes criteria for searching books",
            required = true,
            content = @Content(schema = @Schema(implementation = BookSearchParametersDto.class))
    ) BookSearchParametersDto parametersDto) {
        return bookService.search(parametersDto);
    }
}
