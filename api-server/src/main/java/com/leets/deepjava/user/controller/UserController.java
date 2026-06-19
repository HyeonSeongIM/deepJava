package com.leets.deepjava.user.controller;

import com.leets.deepjava.user.dto.TokenResponse;
import com.leets.deepjava.user.dto.UserLoginRequest;
import com.leets.deepjava.user.dto.UserSignupRequest;
import com.leets.deepjava.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(@RequestBody UserSignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
