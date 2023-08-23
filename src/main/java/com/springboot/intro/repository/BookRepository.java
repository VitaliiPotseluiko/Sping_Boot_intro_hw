package com.springboot.intro.repository;

import com.springboot.intro.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
