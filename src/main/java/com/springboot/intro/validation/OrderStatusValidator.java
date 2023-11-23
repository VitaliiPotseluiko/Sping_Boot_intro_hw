package com.springboot.intro.validation;

import com.springboot.intro.model.Order;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class OrderStatusValidator implements ConstraintValidator<OrderStatus, String> {
    @Override
    public boolean isValid(String status, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(Order.Status.values())
                .map(Order.Status::name)
                .anyMatch(name -> name.equals(status));
    }
}
