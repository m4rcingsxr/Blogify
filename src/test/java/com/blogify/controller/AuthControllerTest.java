package com.blogify.controller;

import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;
import com.blogify.service.CustomerAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String LOGIN_URL = "/api/auth/login";
    private static final String REGISTER_URL = "/api/auth/register";
    private static final String JWT_TOKEN = "jwt token";
    private static final String LOGIN_JSON = "{\"username\":\"testuser\", \"password\":\"testpassword\"}";
    private static final String REGISTER_JSON = "{\"username\":\"newuser\", \"password\":\"newpassword\", \"email\":\"newuser@example.com\"}";

    @MockBean
    private CustomerAuthenticationService authService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenValidLoginRequest_whenLogin_thenReturnsToken() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(JWT_TOKEN);

        mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(LOGIN_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(JWT_TOKEN))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void givenValidRegistrationRequest_whenRegister_thenReturnsCreated() throws Exception {
        String response = "User registered successfully";

        when(authService.register(any(RegistrationRequest.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTER_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(REGISTER_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(response));

        verify(authService, times(1)).register(any(RegistrationRequest.class));
    }
}