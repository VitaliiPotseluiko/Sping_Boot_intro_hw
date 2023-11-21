package com.springboot.intro.service;

import com.springboot.intro.dto.request.AddCartItemRequestDto;
import com.springboot.intro.dto.request.UpdateCartItemRequestDto;
import com.springboot.intro.dto.response.ShoppingCartResponseDto;
import com.springboot.intro.model.User;

public interface ShoppingCartService {
    void registerNewShoppingCart(User user);

    void addBookToShoppingCart(AddCartItemRequestDto requestCartItemDto, User user);
    void updateBookQuantity(UpdateCartItemRequestDto updateCartItemRequestDto,
                            User user,
                            Long cartItemId);

    void deleteBookFromShoppingCart(User user, Long cartItemId);

    ShoppingCartResponseDto getByUser(User user);

}
