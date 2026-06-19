package com.leets.deepjava.user.service;

import com.leets.deepjava.user.domain.Role;
import com.leets.deepjava.user.domain.User;
import com.leets.deepjava.user.domain.exception.InvalidCredentialsException;
import com.leets.deepjava.user.dto.UserLoginRequest;
import com.leets.deepjava.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLoginTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserLogin userLogin;

    private User buildUser() {
        return User.builder()
                .email("a@b.com").password("hashed").nickname("닉").role(Role.USER)
                .build();
    }

    @Test
    void 올바른_자격증명이면_유저를_반환한다() {
        User user = buildUser();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain", "hashed")).thenReturn(true);

        User result = userLogin.authenticate(new UserLoginRequest("a@b.com", "plain"));

        assertThat(result.getEmail()).isEqualTo("a@b.com");
    }

    @Test
    void 이메일이_없으면_예외를_던진다() {
        when(userRepository.findByEmail("unknown@b.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> userLogin.authenticate(new UserLoginRequest("unknown@b.com", "pw")));
    }

    @Test
    void 비밀번호가_틀리면_예외를_던진다() {
        User user = buildUser();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userLogin.authenticate(new UserLoginRequest("a@b.com", "wrong")));
    }
}
