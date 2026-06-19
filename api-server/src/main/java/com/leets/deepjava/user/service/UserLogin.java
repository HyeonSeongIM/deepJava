package com.leets.deepjava.user.service;

import com.leets.deepjava.user.domain.User;
import com.leets.deepjava.user.domain.exception.InvalidCredentialsException;
import com.leets.deepjava.user.dto.UserLoginRequest;
import com.leets.deepjava.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLogin {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return user;
    }
}
