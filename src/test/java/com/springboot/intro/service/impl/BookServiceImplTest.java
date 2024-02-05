package com.springboot.intro.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.springboot.intro.dto.request.BookRequestDto;
import com.springboot.intro.dto.response.BookResponseDto;
import com.springboot.intro.exception.EntityNotFoundException;
import com.springboot.intro.mapper.BookMapper;
import com.springboot.intro.model.Book;
import com.springboot.intro.model.Category;
import com.springboot.intro.repository.BookRepository;
import com.springboot.intro.repository.CategoryRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookServiceImpl bookService;
    private static Category category;
    private static Book book;
    private static BookResponseDto responseDto;

    @BeforeAll
    public static void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Fiction");
        category.setDescription("Fiction books");

        book = new Book();
        book.setId(1L);
        book.setAuthor("Shevchenko");
        book.setTitle("Kobzar");
        book.setIsbn("978-2-266-11156-0");
        book.setPrice(BigDecimal.TEN);
        book.setCategories(Set.of(category));
        book.setDescription("poems");
        book.setCoverImage("coverImage");

        responseDto = new BookResponseDto();
        responseDto.setId(1L);
        responseDto.setAuthor("Shevchenko");
        responseDto.setTitle("Kobzar");
        responseDto.setIsbn("978-2-266-11156-0");
        responseDto.setPrice(BigDecimal.TEN);
        responseDto.setCategoryIds(Set.of(category.getId()));
        responseDto.setDescription("poems");
        responseDto.setCoverImage("coverImage");
    }

    @Test
    @DisplayName("""
            get book where its id = 1
            """)
    public void getBookById_GetBookByIdEqualsOne_ReturnsBookWithIdEqualsOne() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(responseDto);

        BookResponseDto actual = bookService.getBookById(1L);
        assertTrue(EqualsBuilder.reflectionEquals(responseDto, actual));
    }

    @Test
    @DisplayName("""
            get book with not existing id
            """)
    public void getBookById_GetBookByNotExistingId_ReturnsEntityNotFoundException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception actualException = assertThrows(
                EntityNotFoundException.class, () -> bookService.getBookById(4L)
        );

        assertEquals("EntityNotFoundException", actualException.getClass().getSimpleName());
        assertEquals("Can't find book by id 4", actualException.getMessage());
    }

    @Test
    @DisplayName("""
            verify save() method works successfully
            """)
    public void save_SaveBookSuccessfully_ReturnsSavedBook() {
        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setCategoryId(category.getId());

        when(categoryRepository.findById(requestDto.getCategoryId()))
                .thenReturn(Optional.of(category));
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(responseDto);

        BookResponseDto actual = bookService.save(requestDto);
        assertTrue(EqualsBuilder.reflectionEquals(responseDto, actual));
    }

    @Test
    @DisplayName("""
            verify save() method throws exception
            """)
    public void save_SaveBookWithNotExistingCategory_ReturnsEntityNotFoundException() {
        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setCategoryId(4L);

        when(bookMapper.toModel(any())).thenReturn(book);
        when(categoryRepository.findById(requestDto.getCategoryId())).thenReturn(Optional.empty());

        Exception actualException = assertThrows(
                EntityNotFoundException.class, ()-> bookService.save(requestDto)
        );

        assertEquals("EntityNotFoundException", actualException.getClass().getSimpleName());
        assertEquals("There is no category by id 4 Can't create book!",
                actualException.getMessage());
    }

    @Test
    @DisplayName("""
            verify findAll() method works correctly
            """)
    public void findAll_GetAllBooks_ReturnsAllExistingBooks() {
        List<Book> books = List.of(book);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(responseDto);

        List<BookResponseDto> actual = bookService.findAll(pageable);

        assertEquals(1, actual.size());
        assertEquals(responseDto, actual.get(0));
    }

    @Test
    @DisplayName("""
            update book with id = 1
            """)
    public void update_UpdateBookByIdEqualsOne_ReturnsUpdatedBook() {
        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setAuthor(book.getAuthor());
        requestDto.setIsbn(book.getIsbn());
        requestDto.setPrice(BigDecimal.valueOf(40));
        requestDto.setCategoryId(book.getId());
        requestDto.setDescription(book.getDescription());
        requestDto.setTitle(book.getTitle());
        requestDto.setCoverImage(book.getCoverImage());

        Book updatedBook = new Book();
        updatedBook.setId(book.getId());
        updatedBook.setAuthor(book.getAuthor());
        updatedBook.setIsbn(book.getIsbn());
        updatedBook.setPrice(BigDecimal.valueOf(40));
        updatedBook.setCategories(Set.of(category));
        updatedBook.setDescription(book.getDescription());
        updatedBook.setTitle(book.getTitle());
        updatedBook.setCoverImage(book.getCoverImage());

        BookResponseDto updatedBookResponseDto = new BookResponseDto();
        updatedBookResponseDto.setId(updatedBook.getId());
        updatedBookResponseDto.setAuthor(book.getAuthor());
        updatedBookResponseDto.setIsbn(book.getIsbn());
        updatedBookResponseDto.setPrice(BigDecimal.valueOf(40));
        updatedBookResponseDto.setCategoryIds(Set.of(book.getId()));
        updatedBookResponseDto.setDescription(book.getDescription());
        updatedBookResponseDto.setTitle(book.getTitle());
        updatedBookResponseDto.setCoverImage(book.getCoverImage());

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(updatedBook));
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(updatedBookResponseDto);

        BookResponseDto actual = bookService.update(1L, requestDto);

        assertEquals(book.getId(), actual.getId());
        assertEquals(book.getAuthor(), actual.getAuthor());
        assertEquals(book.getTitle(), actual.getTitle());
        assertEquals(updatedBook.getPrice(), actual.getPrice());
    }

    @Test
    @DisplayName("""
            update book with not existing id
            """)
    public void update_UpdateBookByNotExistingId_ReturnsEntityNotFoundException() {
        when(bookRepository.existsById(7L)).thenReturn(false);

        Exception actualException = assertThrows(
                EntityNotFoundException.class, () -> bookService.update(7L, any())
        );

        assertEquals("EntityNotFoundException", actualException.getClass().getSimpleName());
        assertEquals("Can't update book by id 7. There is no such entity",
                actualException.getMessage());
    }
}