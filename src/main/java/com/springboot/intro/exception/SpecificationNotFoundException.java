package com.springboot.intro.exception;

public class SpecificationNotFoundException extends RuntimeException {
    public SpecificationNotFoundException(String message) {
        super(message);
    }
}
