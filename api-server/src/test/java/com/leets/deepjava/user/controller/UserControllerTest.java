package com.leets.deepjava.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leets.deepjava.user.domain.exception.DuplicateEmailException;
import com.leets.deepjava.user.domain.exception.InvalidCredentialsException;
import com.leets.deepjava.user.dto.TokenResponse;
import com.leets.deepjava.user.dto.UserLoginRequest;
import com.leets.deepjava.user.dto.UserSignupRequest;
import com.leets.deepjava.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean UserService userService;

    @Test
    void signup_성공시_201과_토큰을_반환한다() throws Exception {
        when(userService.signup(any(UserSignupRequest.class)))
                .thenReturn(new TokenResponse("jwt-token"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserSignupRequest("a@b.com", "pw", "닉"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_성공시_200과_토큰을_반환한다() throws Exception {
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(new TokenResponse("jwt-token"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserLoginRequest("a@b.com", "pw"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void signup_이메일_중복시_400을_반환한다() throws Exception {
        when(userService.signup(any(UserSignupRequest.class)))
                .thenThrow(new DuplicateEmailException("a@b.com"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserSignupRequest("a@b.com", "pw", "닉"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void login_인증실패시_401을_반환한다() throws Exception {
        when(userService.login(any(UserLoginRequest.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserLoginRequest("a@b.com", "wrong"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }
}
