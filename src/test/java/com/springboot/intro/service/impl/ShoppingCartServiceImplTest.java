package com.springboot.intro.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartServiceImpl;

    private static User user;
    private static ShoppingCart shoppingCart;
    private static Book book;
    private static CartItem cartItem;

    @BeforeAll
    public static void setUp() {
        user = new User();
        user.setId(1L);

        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);

        book = new Book();
        book.setId(1L);
        book.setAuthor("Shevchenko");
        book.setTitle("Kobzar");

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
    }

    @Test
    public void registerNewShoppingCart_createShoppingCart_Success() {
        when(shoppingCartRepository.save(any())).thenReturn(shoppingCart);

        shoppingCartServiceImpl.registerNewShoppingCart(user);

        assertNotNull(shoppingCart.getUser());
        assertEquals(1L, shoppingCart.getId());
    }

    @Test
    public void addBookToShoppingCart_addOneBook_Success() {
        AddCartItemRequestDto requestDto = new AddCartItemRequestDto();
        requestDto.setBookId(1L);
        requestDto.setQuantity(3);

        CartItem savedCartItem = new CartItem();
        savedCartItem.setId(cartItem.getId());
        savedCartItem.setShoppingCart(cartItem.getShoppingCart());
        savedCartItem.setBook(cartItem.getBook());
        savedCartItem.setQuantity(requestDto.getQuantity());

        ShoppingCart updatedShoppingCart = new ShoppingCart();
        updatedShoppingCart.setId(shoppingCart.getId());
        updatedShoppingCart.setUser(shoppingCart.getUser());
        updatedShoppingCart.setCartItems(Set.of(savedCartItem));

        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(cartItemRepository.save(any())).thenReturn(savedCartItem);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(updatedShoppingCart);

        shoppingCartServiceImpl.addBookToShoppingCart(requestDto, user);

        assertFalse(updatedShoppingCart.getCartItems().isEmpty());
        assertEquals(1, updatedShoppingCart.getCartItems().size());
        assertTrue(updatedShoppingCart.getCartItems().contains(savedCartItem));
    }

    @Test
    public void addBookToShoppingCart_wrongUser_returnEntityNotFoundException() {
        when(shoppingCartRepository.findByUser(new User())).thenReturn(Optional.empty());

        Exception actualException = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartServiceImpl.addBookToShoppingCart(new AddCartItemRequestDto(), new User()));

        assertEquals("EntityNotFoundException", actualException.getClass().getSimpleName());
        assertEquals("Can't find shopping cart for user null", actualException.getMessage());
    }

    @Test
    public void addBookToShoppingCart_wrongBook_returnEntityNotFoundException() {
        AddCartItemRequestDto requestDto = new AddCartItemRequestDto();
        requestDto.setBookId(10L);
        requestDto.setQuantity(3);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        Exception actualException = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartServiceImpl.addBookToShoppingCart(requestDto, user));

        assertEquals("EntityNotFoundException", actualException.getClass().getSimpleName());
        assertEquals("Can't find book by id 10", actualException.getMessage());
    }

    @Test
    public void updateBookQuantity_updateCartItemByIdEqualsOne_Success() {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(4);
        ShoppingCart currentShoppingCart = new ShoppingCart();
        currentShoppingCart.setId(1L);
        currentShoppingCart.setUser(user);
        currentShoppingCart.setCartItems(Set.of(cartItem));

        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setId(cartItem.getId());
        updatedCartItem.setShoppingCart(cartItem.getShoppingCart());
        updatedCartItem.setBook(cartItem.getBook());
        updatedCartItem.setQuantity(requestDto.getQuantity());

        ShoppingCart updatedShoppingCart = new ShoppingCart();
        updatedShoppingCart.setId(shoppingCart.getId());
        updatedShoppingCart.setUser(shoppingCart.getUser());
        updatedShoppingCart.setCartItems(Set.of(updatedCartItem));

        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(currentShoppingCart));
        when(shoppingCartRepository.save(currentShoppingCart)).thenReturn(updatedShoppingCart);

        shoppingCartServiceImpl.updateBookQuantity(requestDto, user, 1L);

        assertEquals(4, updatedShoppingCart.getCartItems().stream()
                .findFirst()
                .get()
                .getQuantity());
    }

    @Test
    public void updateBookQuantity_notExistingId_returnEntityNotFoundException() {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(4);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));

        Exception actualException = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartServiceImpl.updateBookQuantity(requestDto, user, 2L));

        assertEquals("EntityNotFoundException", actualException.getClass().getSimpleName());
        assertEquals("Can't find cart item by id 2", actualException.getMessage());
    }

    @Test
    public void deleteBookFromShoppingCart_deleteCartItemByIdEqualsOne_Success() {
        Set<CartItem> cartItemSet = new HashSet<>();
        cartItemSet.add(cartItem);
        ShoppingCart currentShoppingCart = new ShoppingCart();
        currentShoppingCart.setId(1L);
        currentShoppingCart.setUser(user);
        currentShoppingCart.setCartItems(cartItemSet);

        ShoppingCart updatedShoppingCart = new ShoppingCart();
        updatedShoppingCart.setId(1L);
        updatedShoppingCart.setUser(user);
        updatedShoppingCart.setCartItems(Collections.emptySet());

        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(currentShoppingCart));
        when(shoppingCartRepository.save(currentShoppingCart)).thenReturn(updatedShoppingCart);

        shoppingCartServiceImpl.deleteBookFromShoppingCart(user, 1L);

        assertTrue(updatedShoppingCart.getCartItems().isEmpty());
    }

    @Test
    public void deleteBookFromShoppingCart_notExistingId_returnEntityNotFoundException() {
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));

        Exception actualException = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartServiceImpl.deleteBookFromShoppingCart(user, 1L));

        assertEquals("EntityNotFoundException", actualException.getClass().getSimpleName());
        assertEquals("Can't find cart item by id 1", actualException.getMessage());
    }

    @Test
    public void clearShoppingCart_deleteAllCartItems_Success() {
        Set<CartItem> cartItemSet = new HashSet<>();
        cartItemSet.add(cartItem);
        cartItemSet.add(new CartItem());
        ShoppingCart currentShoppingCart = new ShoppingCart();
        currentShoppingCart.setId(1L);
        currentShoppingCart.setUser(user);
        currentShoppingCart.setCartItems(cartItemSet);

        ShoppingCart updatedShoppingCart = new ShoppingCart();
        updatedShoppingCart.setId(1L);
        updatedShoppingCart.setUser(user);
        updatedShoppingCart.setCartItems(Collections.emptySet());

        when(shoppingCartRepository.save(any())).thenReturn(updatedShoppingCart);

        shoppingCartServiceImpl.clearShoppingCart(currentShoppingCart);

        assertTrue(updatedShoppingCart.getCartItems().isEmpty());
    }

    @Test
    public void getByUser_existingUser_Success() {
        ShoppingCartResponseDto responseDto = new ShoppingCartResponseDto();
        responseDto.setId(shoppingCart.getId());
        responseDto.setUserId(user.getId());
        responseDto.setCartItemSet(Collections.emptySet());
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(responseDto);

        shoppingCartServiceImpl.getByUser(user);

        assertEquals(1, responseDto.getId());
        assertEquals(1, responseDto.getUserId());
        assertTrue(responseDto.getCartItemSet().isEmpty());
    }
}