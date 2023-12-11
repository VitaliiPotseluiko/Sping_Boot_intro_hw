package com.springboot.intro.specification.provider;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    String getKey();
    Specification<T> getSpecification(String[] params);
}
