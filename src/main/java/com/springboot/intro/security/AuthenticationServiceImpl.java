package com.springboot.intro.security;

import com.springboot.intro.dto.request.UserLoginRequestDto;
import com.springboot.intro.dto.request.UserRegistrationRequestDto;
import com.springboot.intro.dto.response.UserLoginResponseDto;
import com.springboot.intro.dto.response.UserResponseDto;
import com.springboot.intro.exception.RegistrationException;
import com.springboot.intro.mapper.UserMapper;
import com.springboot.intro.model.Role;
import com.springboot.intro.model.User;
import com.springboot.intro.repository.RoleRepository;
import com.springboot.intro.repository.UserRepository;
import com.springboot.intro.security.jwt.JwtUtil;
import java.util.HashSet;
import java.util.Set;

import com.springboot.intro.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ShoppingCartService shoppingCartService;

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword())
        );
        String token = jwtUtil.generateToken(requestDto.getEmail());
        return new UserLoginResponseDto(token);
    }

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User is already registered!");
        }
        User user = userMapper.toModel(requestDto);
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(Role.RoleName.USER));
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        User savedUser = userRepository.save(user);
        shoppingCartService.registerNewShoppingCart(savedUser);
        return userMapper.toDto(user);
    }
}
