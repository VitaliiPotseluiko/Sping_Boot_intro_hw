package com.springboot.intro;

import com.springboot.intro.model.Book;
import com.springboot.intro.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IntroApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(IntroApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book book = new Book();
                book.setTitle("Kobzar");
                book.setAuthor("Shevchenko");
                book.setIsbn("122134346464");
                book.setPrice(BigDecimal.valueOf(100));

                bookService.save(book);
                bookService.findAll().forEach(System.out::println);
            }
        };
    }

}
