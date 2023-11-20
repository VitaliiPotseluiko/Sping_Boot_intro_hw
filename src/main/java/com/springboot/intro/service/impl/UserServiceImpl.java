package com.springboot.intro.service.impl;

import com.springboot.intro.exception.EntityNotFoundException;
import com.springboot.intro.model.User;
import com.springboot.intro.repository.UserRepository;
import com.springboot.intro.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getUserByName(String username) {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("Can't find user in DB!")
        );
    }
}
