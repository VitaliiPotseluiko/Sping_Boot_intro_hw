package com.springboot.intro.dto.request;

import com.springboot.intro.validation.Email;
import com.springboot.intro.validation.FieldMatch;
import com.springboot.intro.validation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@FieldMatch
public class UserRegistrationRequestDto {
    @NotBlank(message = "email can't be blank")
    @Email
    private String email;
    @NotBlank(message = "password can't be blank")
    @Password
    private String password;
    @NotBlank(message = "repeated password can't be blank")
    private String repeatedPassword;
    @NotBlank(message = "first name can't be blank")
    private String firstName;
    @NotBlank(message = "last name can't be blank")
    private String lastName;
    private String shippingAddress;
}
