package com.springboot.intro.repository;

import com.springboot.intro.model.ShoppingCart;
import com.springboot.intro.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);

    @Query("SELECT shc FROM ShoppingCart shc INNER JOIN shc.cartItems WHERE shc.user = :user")
    Optional<ShoppingCart> findByUserWithCartItems(User user);
}
