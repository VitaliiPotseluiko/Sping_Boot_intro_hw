package com.springboot.intro.controller;

import com.springboot.intro.dto.request.CreateOrderRequestDto;
import com.springboot.intro.dto.request.UpdateOrderStatusRequestDto;
import com.springboot.intro.dto.response.OrderItemResponseDto;
import com.springboot.intro.dto.response.OrderResponseDto;
import com.springboot.intro.model.User;
import com.springboot.intro.service.OrderService;
import com.springboot.intro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    @Operation(summary = "Create order", description = "create a new order")
    public OrderResponseDto createOrder(
            Authentication authentication,
            @Parameter(
                    description = "Object for creating a new order",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateOrderRequestDto.class))
            ) @RequestBody @Valid CreateOrderRequestDto createOrderRequestDto) {
        User user = getUser(authentication);
        return orderService.createOrder(user, createOrderRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    @Operation(summary = "get orders", description = "get all orders of certain user")
    public List<OrderResponseDto> getAllOrders(Authentication authentication) {
        return orderService.getAllOrdersByUser(getUser(authentication));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "get books", description = "get books by order id of certain user")
    public List<OrderItemResponseDto> getAllBooksOfCertainOrder(
            Authentication authentication,
            @Parameter(
                    description = "id of certain order",
                    name = "id",
                    required = true,
                    example = "1"
            ) @PathVariable Long orderId) {
        return orderService.getAllBooksByOrderId(getUser(authentication), orderId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "get book", description = "get book by id of certain order")
    public OrderItemResponseDto getItemByIdOfCertainOrder(
            Authentication authentication,
            @Parameter(
                    description = "id of certain order",
                    name = "id",
                    required = true,
                    example = "1"
            ) @PathVariable Long orderId,
            @Parameter(
                    description = "id of certain order item",
                    name = "id",
                    required = true,
                    example = "1"
            ) @PathVariable Long itemId) {
        return orderService.getBookByIdOfCertainOrder(getUser(authentication), orderId, itemId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{orderId}")
    @Operation(summary = "update status", description = "update order status")
    public OrderResponseDto updateOrderStatus(@Parameter(
            description = "id of certain order",
            name = "id",
            required = true,
            example = "1"
    ) @PathVariable Long orderId, @Parameter(
            description = "Object for updating a new order",
            required = true,
            content = @Content(schema = @Schema(implementation = UpdateOrderStatusRequestDto.class))
    ) @RequestBody @Valid UpdateOrderStatusRequestDto requestDto) {
        return orderService.updateOrderStatus(orderId, requestDto);
    }

    private User getUser(Authentication authentication) {
        return userService.getUserByName(authentication.getName());
    }
}
