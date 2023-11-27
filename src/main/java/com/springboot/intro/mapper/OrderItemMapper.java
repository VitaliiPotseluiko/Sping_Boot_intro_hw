package com.springboot.intro.mapper;

import com.springboot.intro.config.MapperConfig;
import com.springboot.intro.dto.response.OrderItemResponseDto;
import com.springboot.intro.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "quantity", target = "quantity")
    OrderItemResponseDto toDto(OrderItem orderItem);
}
