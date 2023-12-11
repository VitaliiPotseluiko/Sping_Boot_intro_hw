package com.springboot.intro.specification;

import com.springboot.intro.specification.provider.SpecificationProvider;

public interface SpecificationProviderManager<T> {
    SpecificationProvider<T> getSpecificationProvider(String key);
}
