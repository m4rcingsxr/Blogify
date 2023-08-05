package com.blogify.service;

import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;

public interface CustomerAuthenticationService {

    String login(LoginRequest loginDto);

    String register(RegistrationRequest registerDto);

}