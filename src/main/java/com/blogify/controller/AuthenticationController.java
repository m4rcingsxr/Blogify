package com.blogify.controller;

import com.blogify.payload.JWTResponse;
import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;
import com.blogify.service.CustomerAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final CustomerAuthenticationService authService;

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JWTResponse> login(@Valid @RequestBody LoginRequest loginDto) {
        String token = authService.login(loginDto);

        JWTResponse jwtAuthResponse = new JWTResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest registerDto) {
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}