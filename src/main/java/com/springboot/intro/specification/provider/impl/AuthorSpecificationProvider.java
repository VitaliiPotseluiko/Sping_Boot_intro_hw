package com.springboot.intro.specification.provider.impl;

import com.springboot.intro.model.Book;
import com.springboot.intro.specification.provider.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "author";
    }

    @Override
    public Specification<Book> getSpecification(String[] authors) {
        return (root, query, criteriaBuilder) -> root
                .get("author")
                .in(Arrays.stream(authors).toArray());
    }
}
