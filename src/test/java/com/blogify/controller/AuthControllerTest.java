package com.blogify.controller;

import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;
import com.blogify.service.CustomerAuthenticationService;
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

    @MockBean
    private CustomerAuthenticationService authService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenValidLoginRequest_whenLogin_thenReturnsToken() throws Exception {

        // Given
        String token = "jwt token";
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser");
        loginRequest.setPassword("testpassword");

        when(authService.login(any(LoginRequest.class))).thenReturn(token);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"testuser\", \"password\":\"testpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(token))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void givenValidRegistrationRequest_whenRegister_thenReturnsCreated() throws Exception {
        // Given
        String response = "User registered successfully";
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("newuser");
        registrationRequest.setPassword("newpassword");
        registrationRequest.setEmail("newuser@example.com");

        when(authService.register(any(RegistrationRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"newuser\", \"password\":\"newpassword\", \"email\":\"newuser@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(response));
    }

}