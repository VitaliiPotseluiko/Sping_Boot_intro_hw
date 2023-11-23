package com.springboot.intro.repository;

import com.springboot.intro.model.Order;
import com.springboot.intro.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser(User user);

    Optional<Order> findOrderByUserAndId(User user, Long id);

    Optional<Order> findOrderById(Long id);
}
