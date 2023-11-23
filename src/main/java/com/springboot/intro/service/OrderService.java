package com.springboot.intro.service;

import com.springboot.intro.dto.request.CreateOrderRequestDto;
import com.springboot.intro.dto.request.UpdateOrderStatusRequestDto;
import com.springboot.intro.dto.response.OrderItemResponseDto;
import com.springboot.intro.dto.response.OrderResponseDto;
import com.springboot.intro.model.User;
import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(User user, CreateOrderRequestDto requestDto);

    OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto);

    List<OrderResponseDto> getAllOrdersByUser(User user);

    List<OrderItemResponseDto> getAllBooksByOrderId(User user, Long orderId);

    OrderItemResponseDto getBookByIdOfCertainOrder(User user, Long orderId, Long itemId);
}
