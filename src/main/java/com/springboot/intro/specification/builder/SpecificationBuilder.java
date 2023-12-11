package com.springboot.intro.specification.builder;

import com.springboot.intro.dto.request.BookSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParametersDto parametersDto);
}
