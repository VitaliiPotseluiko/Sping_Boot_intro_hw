package com.springboot.intro.specification.provider.impl;

import com.springboot.intro.model.Book;
import com.springboot.intro.specification.provider.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    public static final String TITLE_KEY = "title";
    @Override
    public String getKey() {
        return TITLE_KEY;
    }

    @Override
    public Specification<Book> getSpecification(String[] titles) {
        return ((root, query, criteriaBuilder) -> root
                .get(TITLE_KEY)
                .in(Arrays.stream(titles).toArray()));
    }
}
