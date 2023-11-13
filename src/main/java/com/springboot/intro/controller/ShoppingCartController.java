package com.springboot.intro.controller;

import com.springboot.intro.dto.request.AddRequestCartItemDto;
import com.springboot.intro.dto.response.ShoppingCartResponseDto;
import com.springboot.intro.model.User;
import com.springboot.intro.service.ShoppingCartService;
import com.springboot.intro.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public ShoppingCartResponseDto getShoppingCart(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByName(username);
        return shoppingCartService.getByUser(user);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ShoppingCartResponseDto addBookToShoppingCart(
            Authentication authentication,
            @RequestBody @Valid AddRequestCartItemDto cartItemDto) {
        String username = authentication.getName();
        User user = userService.getUserByName(username);
        shoppingCartService.addBookToShoppingCart(cartItemDto, user);
        return shoppingCartService.getByUser(user);
    }
}
