package com.springboot.intro.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponseDto {
    private Long id;
    private Long bookId;
    private int quantity;
}
