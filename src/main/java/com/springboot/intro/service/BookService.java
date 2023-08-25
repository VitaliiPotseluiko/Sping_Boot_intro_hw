package com.springboot.intro.service;

import com.springboot.intro.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
