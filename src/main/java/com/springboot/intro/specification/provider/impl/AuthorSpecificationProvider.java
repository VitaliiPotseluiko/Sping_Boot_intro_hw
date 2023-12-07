package com.springboot.intro.specification.provider.impl;

import com.springboot.intro.model.Book;
import com.springboot.intro.specification.provider.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    public static final String AUTHOR_KEY = "author";
    @Override
    public String getKey() {
        return AUTHOR_KEY;
    }

    @Override
    public Specification<Book> getSpecification(String[] authors) {
        return (root, query, criteriaBuilder) -> root
                .get(AUTHOR_KEY)
                .in(Arrays.stream(authors).toArray());
    }
}
