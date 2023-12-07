package com.springboot.intro.specification.builder.impl;

import com.springboot.intro.dto.request.BookSearchParametersDto;
import com.springboot.intro.model.Book;
import com.springboot.intro.specification.SpecificationProviderManager;
import com.springboot.intro.specification.builder.SpecificationBuilder;
import com.springboot.intro.specification.provider.impl.AuthorSpecificationProvider;
import com.springboot.intro.specification.provider.impl.DescriptionSpecificationProvider;
import com.springboot.intro.specification.provider.impl.TitleSpecificationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto parametersDto) {
        Specification<Book> specification = Specification.where(null);
        if (parametersDto.getAuthors() != null && parametersDto.getAuthors().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(AuthorSpecificationProvider.AUTHOR_KEY)
                    .getSpecification(parametersDto.getAuthors()));
        }
        if (parametersDto.getDescriptions() != null && parametersDto.getDescriptions().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(DescriptionSpecificationProvider.DESCRIPTION_KEY)
                    .getSpecification(parametersDto.getDescriptions()));
        }
        if (parametersDto.getTitles() != null && parametersDto.getTitles().length > 0) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(TitleSpecificationProvider.TITLE_KEY)
                    .getSpecification(parametersDto.getTitles()));
        }
        return specification;
    }
}
