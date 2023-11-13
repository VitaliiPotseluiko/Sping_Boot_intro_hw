package com.springboot.intro.mapper;

import com.springboot.intro.config.MapperConfig;
import com.springboot.intro.dto.response.ShoppingCartResponseDto;
import com.springboot.intro.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItemSet")
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);
}
