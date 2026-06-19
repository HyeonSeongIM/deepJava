package com.leets.deepjava.user.service;

import com.leets.deepjava.user.domain.Role;
import com.leets.deepjava.user.domain.User;
import com.leets.deepjava.user.dto.UserSignupRequest;
import com.leets.deepjava.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCreateTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserCreate userCreate;

    @Test
    void 비밀번호를_BCrypt로_해싱하여_저장한다() {
        UserSignupRequest request = new UserSignupRequest("a@b.com", "plain", "닉네임");
        when(passwordEncoder.encode("plain")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userCreate.create(request);

        assertThat(result.getPassword()).isEqualTo("hashed");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        verify(passwordEncoder).encode("plain");
    }

    @Test
    void 이메일과_닉네임이_그대로_저장된다() {
        UserSignupRequest request = new UserSignupRequest("a@b.com", "plain", "닉네임");
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userCreate.create(request);

        assertThat(result.getEmail()).isEqualTo("a@b.com");
        assertThat(result.getNickname()).isEqualTo("닉네임");
    }
}
