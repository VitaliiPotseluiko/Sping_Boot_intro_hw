package com.springboot.intro.controller;

import com.springboot.intro.dto.request.UserLoginRequestDto;
import com.springboot.intro.dto.request.UserRegistrationRequestDto;
import com.springboot.intro.dto.response.UserLoginResponseDto;
import com.springboot.intro.dto.response.UserResponseDto;
import com.springboot.intro.exception.RegistrationException;
import com.springboot.intro.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return authenticationService.register(requestDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.login(requestDto);
    }

}
