package com.springboot.intro.service.impl;

import com.springboot.intro.dto.request.AddRequestCartItemDto;
import com.springboot.intro.dto.response.ShoppingCartResponseDto;
import com.springboot.intro.exception.EntityNotFoundException;
import com.springboot.intro.mapper.ShoppingCartMapper;
import com.springboot.intro.model.Book;
import com.springboot.intro.model.CartItem;
import com.springboot.intro.model.ShoppingCart;
import com.springboot.intro.model.User;
import com.springboot.intro.repository.BookRepository;
import com.springboot.intro.repository.CartItemRepository;
import com.springboot.intro.repository.ShoppingCartRepository;
import com.springboot.intro.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public void registerNewShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        Set<CartItem> cartItems = new HashSet<>();
        shoppingCart.setCartItems(cartItems);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void addBookToShoppingCart(AddRequestCartItemDto requestCartItemDto, User user) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart by user "
                        + user.getLastName()));
        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(bookRepository.findById(requestCartItemDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id "
                        + requestCartItemDto.getBookId())
        ));
        cartItem.setQuantity(requestCartItemDto.getQuantity());
        shoppingCart.getCartItems().add(cartItemRepository.save(cartItem));
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto getByUser(User user) {
        return shoppingCartMapper.toDto(shoppingCartRepository.findByUser(user).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart!"
                        + " There is no such user " + user)));
    }
}
