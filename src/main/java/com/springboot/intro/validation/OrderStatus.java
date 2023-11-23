package com.springboot.intro.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OrderStatusValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderStatus {
    String message() default "Invalid status name! Valid statuses: COMPLETED, PENDING, DELIVERED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
