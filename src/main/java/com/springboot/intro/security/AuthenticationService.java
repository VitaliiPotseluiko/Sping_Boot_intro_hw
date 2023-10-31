package com.springboot.intro.security;

import com.springboot.intro.dto.request.UserLoginRequestDto;
import com.springboot.intro.dto.request.UserRegistrationRequestDto;
import com.springboot.intro.dto.response.UserLoginResponseDto;
import com.springboot.intro.dto.response.UserResponseDto;
import com.springboot.intro.exception.RegistrationException;

public interface AuthenticationService {
    UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto);

    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
