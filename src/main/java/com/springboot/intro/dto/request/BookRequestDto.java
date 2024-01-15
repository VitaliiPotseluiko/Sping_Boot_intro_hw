package com.springboot.intro.dto.request;

import java.math.BigDecimal;
import java.util.Set;

import com.springboot.intro.model.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.ISBN;

@Data
@Accessors(chain = true)
public class BookRequestDto {
    @NotBlank(message = "can't be blank")
    private String title;
    @NotBlank(message = "can't be blank")
    private String author;
    @NotBlank(message = "can't be blank")
    @ISBN
    private String isbn;
    @NotNull
    @Min(value = 0, message = "must be bigger than 0")
    private BigDecimal price;
    private String description;
    private String coverImage;
    @NotNull
    @Min(1)
    private Long categoryId;
}
