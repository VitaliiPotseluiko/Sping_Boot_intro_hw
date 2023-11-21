package com.springboot.intro.service;

import com.springboot.intro.model.User;

public interface UserService {
    User getUserByName(String username);
}
