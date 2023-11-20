package com.springboot.intro.controller;

import com.springboot.intro.dto.request.AddCartItemRequestDto;
import com.springboot.intro.dto.request.UpdateCartItemRequestDto;
import com.springboot.intro.dto.response.ShoppingCartResponseDto;
import com.springboot.intro.model.User;
import com.springboot.intro.service.ShoppingCartService;
import com.springboot.intro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get shopping cart", description = "Get shopping cart by certain user")
    public ShoppingCartResponseDto getShoppingCart(Authentication authentication) {
        User user = getUser(authentication);
        return shoppingCartService.getByUser(user);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    @Operation(summary = "Add a book", description = "Add a book to shopping cart")
    public ShoppingCartResponseDto addBookToShoppingCart(
            Authentication authentication,
            @Parameter(
                    description = "Object for adding to shopping cart",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AddCartItemRequestDto.class))
            ) @RequestBody @Valid AddCartItemRequestDto cartItemDto) {
        User user = getUser(authentication);
        shoppingCartService.addBookToShoppingCart(cartItemDto, user);
        return shoppingCartService.getByUser(user);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Update book quantity",
            description = "Update book quantity of certain book in user's shopping cart")
    public ShoppingCartResponseDto updateBookQuantity(
            Authentication authentication,
            @Parameter(
                    description = "Object for updating book quantity in shopping cart",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateCartItemRequestDto.class))
            ) @RequestBody @Valid UpdateCartItemRequestDto updateCartItemRequestDto,
            @Parameter(
                    description = "id of certain cart item of shopping cart",
                    name = "cartItemId",
                    required = true,
                    example = "1"
            ) @PathVariable Long cartItemId) {
        User user = getUser(authentication);
        shoppingCartService.updateBookQuantity(updateCartItemRequestDto, user, cartItemId);
        return shoppingCartService.getByUser(user);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Delete book from shopping cart",
            description = "Delete book from shopping cart of certain user")
    public ShoppingCartResponseDto deleteBookFromShoppingCart(
            Authentication authentication,
            @Parameter(
                    description = "id of certain cart item of shopping cart",
                    name = "cartItemId",
                    required = true,
                    example = "1"
            ) @PathVariable Long cartItemId) {
        User user = getUser(authentication);
        shoppingCartService.deleteBookFromShoppingCart(user, cartItemId);
        return shoppingCartService.getByUser(user);
    }

    private User getUser(Authentication authentication) {
        return userService.getUserByName(authentication.getName());
    }

}
