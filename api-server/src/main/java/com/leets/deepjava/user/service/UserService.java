package com.leets.deepjava.user.service;

import com.leets.deepjava.user.domain.User;
import com.leets.deepjava.user.dto.TokenResponse;
import com.leets.deepjava.user.dto.UserLoginRequest;
import com.leets.deepjava.user.dto.UserSignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserValidator userValidator;
    private final UserCreate userCreate;
    private final UserLogin userLogin;
    private final JwtProvider jwtProvider;

    public TokenResponse signup(UserSignupRequest request) {
        userValidator.validateEmailNotDuplicated(request.email());
        User user = userCreate.create(request);
        return new TokenResponse(jwtProvider.generate(user.getId(), user.getRole()));
    }

    public TokenResponse login(UserLoginRequest request) {
        User user = userLogin.authenticate(request);
        return new TokenResponse(jwtProvider.generate(user.getId(), user.getRole()));
    }
}
