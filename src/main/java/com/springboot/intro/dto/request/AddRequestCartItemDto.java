package com.springboot.intro.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddRequestCartItemDto {
    @NotNull
    private Long bookId;
    @NotNull
    @Min(value = 1)
    private int quantity;
}
