package com.leets.deepjava.user.service;

import com.leets.deepjava.user.domain.Role;
import com.leets.deepjava.user.domain.User;
import com.leets.deepjava.user.dto.UserSignupRequest;
import com.leets.deepjava.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreate {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(UserSignupRequest request) {
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }
}
