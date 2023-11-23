package com.springboot.intro.service.impl;

import com.springboot.intro.dto.request.CreateOrderRequestDto;
import com.springboot.intro.dto.request.UpdateOrderStatusRequestDto;
import com.springboot.intro.dto.response.OrderItemResponseDto;
import com.springboot.intro.dto.response.OrderResponseDto;
import com.springboot.intro.exception.EntityNotFoundException;
import com.springboot.intro.mapper.OrderMapper;
import com.springboot.intro.model.CartItem;
import com.springboot.intro.model.Order;
import com.springboot.intro.model.OrderItem;
import com.springboot.intro.model.ShoppingCart;
import com.springboot.intro.model.User;
import com.springboot.intro.repository.OrderItemRepository;
import com.springboot.intro.repository.OrderRepository;
import com.springboot.intro.repository.ShoppingCartRepository;
import com.springboot.intro.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public OrderResponseDto createOrder(User user, CreateOrderRequestDto requestDto) {
        ShoppingCart shoppingCart = getShoppingCart(user);
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.COMPLETED);
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setOrderDate(LocalDateTime.now());
        BigDecimal total = shoppingCart.getCartItems().stream()
                .map(cartItem -> cartItem.getBook()
                        .getPrice()
                        .multiply(new BigDecimal(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);
        Set<OrderItem> orderItems = createOrderItems(shoppingCart, savedOrder);
        savedOrder.setOrderItems(orderItems);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findOrderById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id " + orderId)
        );
        order.setStatus(Order.Status.valueOf(requestDto.getStatus()));
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderResponseDto> getAllOrdersByUser(User user) {
        return orderRepository.findAllByUser(user).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderItemResponseDto> getAllBooksByOrderId(User user, Long orderId) {
        return orderMapper.toDto(orderRepository.findOrderByUserAndId(user, orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't items of order by id " + orderId)))
                .getOrderItems().stream()
                .toList();
    }

    @Override
    public OrderItemResponseDto getBookByIdOfCertainOrder(User user, Long orderId, Long itemId) {
        return getAllBooksByOrderId(user, orderId).stream()
                .filter(orderItemResponseDto -> orderItemResponseDto.getId().equals(itemId))
                .findFirst().orElseThrow(
                        () -> new EntityNotFoundException("Can't find item by id " + itemId));
    }

    private ShoppingCart getShoppingCart(User user) {
        return shoppingCartRepository.findByUser(user).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart for "
                        + "user " + user.getLastName()));
    }

    private Set<OrderItem> createOrderItems(ShoppingCart shoppingCart, Order order) {
        Set<OrderItem> orderItems = new HashSet<>();
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItemRepository.save(orderItem));
        }
        clearShoppingCart(shoppingCart, cartItems);
        return orderItems;
    }

    private void clearShoppingCart(ShoppingCart shoppingCart, Set<CartItem> cartItems) {
        cartItems.clear();
        shoppingCart.setCartItems(cartItems);
        shoppingCartRepository.save(shoppingCart);
    }
}
