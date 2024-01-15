package com.springboot.intro.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequestDto {
    @NotBlank(message = "can't be blank")
    private String name;
    private String description;
}
