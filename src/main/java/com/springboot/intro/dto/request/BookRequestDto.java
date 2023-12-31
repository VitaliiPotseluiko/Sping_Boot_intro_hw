package com.springboot.intro.dto.request;

import java.math.BigDecimal;
import java.util.Set;

import com.springboot.intro.model.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.ISBN;

@Data
public class BookRequestDto {
    @NotBlank(message = "title can't be black")
    private String title;
    @NotBlank(message = "author can't be black")
    private String author;
    @NotBlank(message = "isbn can't be black")
    @ISBN
    private String isbn;
    @NotNull
    @Min(0)
    private BigDecimal price;
    private String description;
    private String coverImage;
    @NotNull
    @Min(1)
    private Long categoryId;
}
