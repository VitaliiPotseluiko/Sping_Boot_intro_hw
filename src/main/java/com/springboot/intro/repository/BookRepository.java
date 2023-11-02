package com.springboot.intro.repository;

import com.springboot.intro.model.Book;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = "select b from Book b inner join b.categories c where c.id = :categoryId")
    List<Book> findAllByCategoryId(Long categoryId, Pageable pageable);
}
