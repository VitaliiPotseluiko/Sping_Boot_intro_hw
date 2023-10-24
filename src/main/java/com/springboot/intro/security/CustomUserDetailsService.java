package com.springboot.intro.security;

import static org.springframework.security.core.userdetails.User.withUsername;

import com.springboot.intro.model.User;
import com.springboot.intro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("There is no user by email " + email));
        UserBuilder userBuilder;
        userBuilder = withUsername(email);
        userBuilder.password(user.getPassword());
        userBuilder.roles(user.getRoles().stream()
                        .map(r -> r.getName().name())
                        .toArray(String[]::new));
        return userBuilder.build();
    }
}
