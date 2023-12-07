package com.springboot.intro.specification.provider.impl;

import com.springboot.intro.model.Book;
import com.springboot.intro.specification.provider.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "title";
    }

    @Override
    public Specification<Book> getSpecification(String[] titles) {
        return ((root, query, criteriaBuilder) -> root
                .get("title")
                .in(Arrays.stream(titles).toArray()));
    }
}
