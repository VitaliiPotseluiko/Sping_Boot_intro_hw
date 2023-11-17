package com.springboot.intro.service;

import com.springboot.intro.dto.request.AddRequestCartItemDto;
import com.springboot.intro.dto.request.UpdatedRequestCartItemDto;
import com.springboot.intro.dto.response.ShoppingCartResponseDto;
import com.springboot.intro.model.User;

public interface ShoppingCartService {
    void registerNewShoppingCart(User user);

    void addBookToShoppingCart(AddRequestCartItemDto requestCartItemDto, User user);
    void updateCartItems(UpdatedRequestCartItemDto updatedRequestCartItemDto,
                         User user,
                         Long cartItemId);

    void deleteCartItem(User user, Long cartItemId);

    ShoppingCartResponseDto getByUser(User user);

}
