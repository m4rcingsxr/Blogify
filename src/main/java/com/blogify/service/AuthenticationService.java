package com.blogify.service;

import com.blogify.payload.JWTResponse;
import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;
import jakarta.mail.MessagingException;

public interface AuthenticationService {

    JWTResponse login(LoginRequest loginDto);

    void register(RegistrationRequest registerDto) throws MessagingException;

    void activate(String token) throws MessagingException;

}