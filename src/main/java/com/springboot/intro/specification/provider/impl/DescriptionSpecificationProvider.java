package com.springboot.intro.specification.provider.impl;

import com.springboot.intro.model.Book;
import com.springboot.intro.specification.provider.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class DescriptionSpecificationProvider implements SpecificationProvider<Book> {
    public static final String DESCRIPTION_KEY = "description";
    @Override
    public String getKey() {
        return DESCRIPTION_KEY;
    }

    @Override
    public Specification<Book> getSpecification(String[] descriptions) {
        return ((root, query, criteriaBuilder) -> root
                .get(DESCRIPTION_KEY)
                .in(Arrays.stream(descriptions).toArray()));
    }
}
