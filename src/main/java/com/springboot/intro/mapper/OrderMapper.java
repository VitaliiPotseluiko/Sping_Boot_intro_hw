package com.springboot.intro.mapper;

import com.springboot.intro.config.MapperConfig;
import com.springboot.intro.dto.response.OrderItemResponseDto;
import com.springboot.intro.dto.response.OrderResponseDto;
import com.springboot.intro.model.Order;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "orderItems", ignore = true)
    OrderResponseDto toDto(Order order);

    @AfterMapping
    default void setOrderItems(@MappingTarget OrderResponseDto responseDto, Order order) {
        Set<OrderItemResponseDto> orderItemResponseDtos = order.getOrderItems().stream()
                .map(orderItem -> OrderItemResponseDto.builder()
                        .id(orderItem.getId())
                        .bookId(orderItem.getBook().getId())
                        .quantity(orderItem.getQuantity())
                        .build())
                .collect(Collectors.toSet());
        responseDto.setOrderItems(orderItemResponseDtos);
    }
}
