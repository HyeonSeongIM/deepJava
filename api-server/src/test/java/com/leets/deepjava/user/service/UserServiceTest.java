package com.leets.deepjava.user.service;

import com.leets.deepjava.user.domain.Role;
import com.leets.deepjava.user.domain.User;
import com.leets.deepjava.user.dto.TokenResponse;
import com.leets.deepjava.user.dto.UserLoginRequest;
import com.leets.deepjava.user.dto.UserSignupRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserValidator userValidator;
    @Mock UserCreate userCreate;
    @Mock UserLogin userLogin;
    @Mock JwtProvider jwtProvider;

    @InjectMocks
    UserService userService;

    private User mockUser() {
        return User.builder()
                .email("a@b.com").password("hashed").nickname("닉").role(Role.USER)
                .build();
    }

    @Test
    void signup_검증후_저장하고_토큰을_반환한다() {
        UserSignupRequest req = new UserSignupRequest("a@b.com", "pw", "닉");
        when(userCreate.create(req)).thenReturn(mockUser());
        when(jwtProvider.generate(any(), eq(Role.USER))).thenReturn("jwt-token");

        TokenResponse resp = userService.signup(req);

        verify(userValidator).validateEmailNotDuplicated("a@b.com");
        assertThat(resp.token()).isEqualTo("jwt-token");
    }

    @Test
    void login_인증후_토큰을_반환한다() {
        UserLoginRequest req = new UserLoginRequest("a@b.com", "pw");
        when(userLogin.authenticate(req)).thenReturn(mockUser());
        when(jwtProvider.generate(any(), eq(Role.USER))).thenReturn("jwt-token");

        TokenResponse resp = userService.login(req);

        assertThat(resp.token()).isEqualTo("jwt-token");
    }
}
