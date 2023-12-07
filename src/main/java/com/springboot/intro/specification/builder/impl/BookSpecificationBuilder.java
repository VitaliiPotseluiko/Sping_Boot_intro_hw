package com.springboot.intro.specification.builder.impl;

import com.springboot.intro.dto.request.BookSearchParametersDto;
import com.springboot.intro.model.Book;
import com.springboot.intro.specification.SpecificationProviderManager;
import com.springboot.intro.specification.builder.SpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto parametersDto) {
        Specification<Book> spec = Specification.where(null);
        if (parametersDto.getAuthors() != null && parametersDto.getAuthors().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider("author")
                    .getSpecification(parametersDto.getAuthors()));
        }
        if (parametersDto.getDescriptions() != null && parametersDto.getDescriptions().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider("description")
                    .getSpecification(parametersDto.getDescriptions()));
        }
        if (parametersDto.getTitles() != null && parametersDto.getTitles().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider("title")
                    .getSpecification(parametersDto.getTitles()));
        }
        return spec;
    }
}
