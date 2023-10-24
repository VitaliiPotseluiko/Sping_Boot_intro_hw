package com.springboot.intro.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-zA-z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{3,16}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        return password != null && Pattern.compile(PASSWORD_PATTERN).matcher(password).matches();
    }
}
