package com.blogify.service;

import com.blogify.payload.JWTResponse;
import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;

public interface AuthenticationService {

    JWTResponse login(LoginRequest loginDto);

    void register(RegistrationRequest registerDto);

    void activate(String token);

}