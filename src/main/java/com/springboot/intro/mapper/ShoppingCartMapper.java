package com.springboot.intro.mapper;

import com.springboot.intro.config.MapperConfig;
import com.springboot.intro.dto.response.CartItemResponseDto;
import com.springboot.intro.dto.response.ShoppingCartResponseDto;
import com.springboot.intro.model.ShoppingCart;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "cartItemSet", ignore = true)
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);

    @AfterMapping
    default void setCartItems(@MappingTarget ShoppingCartResponseDto responseDto,
                              ShoppingCart shoppingCart) {
        Set<CartItemResponseDto> cartItemResponseDtos = shoppingCart.getCartItems().stream()
                .map(cartItem -> CartItemResponseDto.builder()
                        .id(cartItem.getId())
                        .bookId(cartItem.getBook().getId())
                        .bookTitle(cartItem.getBook().getTitle())
                        .quantity(cartItem.getQuantity())
                        .build())
                .collect(Collectors.toSet());
        responseDto.setCartItemSet(cartItemResponseDtos);
    }
}
