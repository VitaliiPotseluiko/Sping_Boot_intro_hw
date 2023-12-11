package com.springboot.intro.dto.request;

import lombok.Data;

@Data
public class BookSearchParametersDto {
    String[] authors;
    String[] titles;
    String[] descriptions;
}
