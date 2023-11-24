package com.springboot.intro.service.impl;

import com.springboot.intro.dto.request.AddCartItemRequestDto;
import com.springboot.intro.dto.request.UpdateCartItemRequestDto;
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
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void addBookToShoppingCart(AddCartItemRequestDto requestCartItemDto, User user) {
        ShoppingCart shoppingCart = getShoppingCart(user);
        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        Book book = bookRepository.findById(requestCartItemDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id "
                        + requestCartItemDto.getBookId()));
        cartItem.setBook(book);
        cartItem.setQuantity(requestCartItemDto.getQuantity());
        shoppingCart.getCartItems().add(cartItemRepository.save(cartItem));
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void updateBookQuantity(UpdateCartItemRequestDto updateCartItemRequestDto,
                                   User user,
                                   Long cartItemId) {
        ShoppingCart shoppingCart = getShoppingCart(user);
        Set<CartItem> cartItemSet = shoppingCart.getCartItems();
        if (cartItemSet.stream()
                .filter(cartItem -> cartItem.getId().equals(cartItemId))
                .findFirst().isEmpty()) {
            throw new EntityNotFoundException("Can't find cart item by id " + cartItemId);
        }
        for (CartItem cartItem : cartItemSet) {
            if (cartItem.getId().equals(cartItemId)) {
                cartItem.setQuantity(updateCartItemRequestDto.getQuantity());
                break;
            }
        }
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void deleteBookFromShoppingCart(User user, Long cartItemId) {
        ShoppingCart shoppingCart = getShoppingCart(user);
        Set<CartItem> cartItemSet = shoppingCart.getCartItems();
        CartItem cartItem = cartItemSet.stream()
                .filter(c -> c.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find cart item by id "
                                + cartItemId)
                );
        cartItemSet.remove(cartItem);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void clearShoppingCart(ShoppingCart shoppingCart) {
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        cartItems.clear();
        shoppingCart.setCartItems(cartItems);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto getByUser(User user) {
        return shoppingCartMapper.toDto(getShoppingCart(user));
    }

    @Override
    public ShoppingCart getShoppingCart(User user) {
        return shoppingCartRepository.findByUser(user).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart for "
                        + "user " + user.getLastName()));
    }
}
