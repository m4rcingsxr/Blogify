package com.blogify.controller;

import com.blogify.payload.JWTResponse;
import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;
import com.blogify.service.CustomerAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Operations related to user authentication")
public class AuthenticationController {

    private final CustomerAuthenticationService authService;

    @Operation(
            summary = "User login",
            description = "Authenticate a user and return a JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully authenticated and returned JWT token"),
                    @ApiResponse(responseCode = "400", description = "Invalid login request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid credentials")
            }
    )
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JWTResponse> login(@Valid @RequestBody LoginRequest loginDto) {
        String token = authService.login(loginDto);

        JWTResponse jwtAuthResponse = new JWTResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @Operation(
            summary = "User registration",
            description = "Register a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully registered new user"),
                    @ApiResponse(responseCode = "400", description = "Invalid registration request"),
                    @ApiResponse(responseCode = "409", description = "User already exists")
            }
    )
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest registerDto) {
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
