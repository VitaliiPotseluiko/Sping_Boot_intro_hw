package com.springboot.intro.repository;

import com.springboot.intro.model.ShoppingCart;
import com.springboot.intro.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);
}
