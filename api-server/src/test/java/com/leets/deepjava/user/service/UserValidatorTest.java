package com.leets.deepjava.user.service;

import com.leets.deepjava.user.domain.exception.DuplicateEmailException;
import com.leets.deepjava.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserValidator userValidator;

    @Test
    void 이메일이_중복되면_예외를_던진다() {
        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
                () -> userValidator.validateEmailNotDuplicated("dup@test.com"));
    }

    @Test
    void 이메일이_중복되지_않으면_정상_통과한다() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateEmailNotDuplicated("new@test.com"));
    }
}
