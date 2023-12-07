package com.springboot.intro.specification.provider.impl;

import com.springboot.intro.model.Book;
import com.springboot.intro.specification.provider.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class DescriptionSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "description";
    }

    @Override
    public Specification<Book> getSpecification(String[] descriptions) {
        return ((root, query, criteriaBuilder) -> root
                .get("description")
                .in(Arrays.stream(descriptions).toArray()));
    }
}
