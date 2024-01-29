package com.springboot.intro.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.springboot.intro.model.Book;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    private static final String INSERT_DATA_SCRIPT
            = "classpath:database/books/add-data-to-database.sql";
    private static final String DELETE_DATA_SCRIPT
            = "classpath:database/books/delete-all-from-database.sql";

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            Find all book where category id = 1
            """)
    @Sql(scripts = INSERT_DATA_SCRIPT,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = DELETE_DATA_SCRIPT,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByCategoryId_CategoryIdEqualOne_ReturnThreeBooks() {
        List<Book> actual = bookRepository.findAllByCategoryId(1L, PageRequest.of(0, 10));

        assertEquals(3, actual.size());
        assertEquals(1L, actual.get(0).getId());
        assertEquals(2L, actual.get(1).getId());
        assertEquals(3L, actual.get(2).getId());
    }

    @Test
    @DisplayName("""
            Find all book where category id = 2
            """)
    @Sql(scripts = INSERT_DATA_SCRIPT,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = DELETE_DATA_SCRIPT,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByCategoryId_CategoryIdEqualTwo_ReturnTwoBooks() {
        List<Book> actual = bookRepository.findAllByCategoryId(2L, PageRequest.of(0, 10));

        assertEquals(2, actual.size());
        assertEquals(4L, actual.get(0).getId());
        assertEquals(5L, actual.get(1).getId());
    }

    @Test
    @DisplayName("""
            Find all book where category id = 3
            """)
    @Sql(scripts = INSERT_DATA_SCRIPT,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = DELETE_DATA_SCRIPT,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByCategoryId_CategoryIdEqualThree_ReturnEmptyList() {
        List<Book> actual = bookRepository.findAllByCategoryId(3L, PageRequest.of(0, 10));

        assertEquals(0, actual.size());
    }
}