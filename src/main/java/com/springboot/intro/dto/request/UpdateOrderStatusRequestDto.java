package com.springboot.intro.dto.request;

import com.springboot.intro.validation.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequestDto {
    @NotNull
    @OrderStatus
    private String status;
}
