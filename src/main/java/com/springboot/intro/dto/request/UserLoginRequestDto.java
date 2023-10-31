package com.springboot.intro.dto.request;

import com.springboot.intro.validation.Email;
import com.springboot.intro.validation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @NotBlank(message = "email can't be blank")
    @Email
    private String email;
    @NotBlank(message = "password can't be blank")
    @Password
    private String password;
}
