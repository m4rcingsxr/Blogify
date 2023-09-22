package com.blogify.controller;

import com.blogify.payload.JWTResponse;
import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;
import com.blogify.payload.ErrorResponse;
import com.blogify.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations related to user authentication")
public class AuthenticationController {

    private final AuthenticationService authService;

    @Operation(
            summary = "User login",
            description = "Authenticate a user and return a JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated and returned JWT token", content = @Content(schema = @Schema(implementation = JWTResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid login request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid credentials", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JWTResponse> login(
            @Parameter(description = "Login request payload", required = true)
            @Valid @RequestBody LoginRequest loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

    @Operation(
            summary = "User registration",
            description = "Register a new user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Successfully registered new user", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid registration request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(
            @Parameter(description = "Registration request payload", required = true)
            @Valid @RequestBody RegistrationRequest registerDto) throws MessagingException {
        authService.register(registerDto);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Activate account",
            description = "Activate a user account with the provided token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully activated account", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid activation token", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid activation token", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/activate-account")
    public ResponseEntity<Void> activate(
            @Parameter(description = "Activation token", required = true, example = "abcdef123456")
            @RequestParam String token) throws MessagingException {
        authService.activate(token);
        return ResponseEntity.ok().build();
    }

}
