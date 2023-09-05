package com.blogify.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterUnitTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsManagerImpl userDetailsManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    public void clean() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void givenValidToken_whenDoFilterInternal_thenAuthenticate() throws ServletException, IOException {

        // Given
        String token = "valid-jwt-token";
        String email = "testuser@example.com";
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                email,
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/some-path");
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(userDetailsManager.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
        assertIterableEquals(userDetails.getAuthorities(), authentication.getAuthorities());
        verify(jwtService).extractUsername(token);
        verify(jwtService).isTokenValid(token, userDetails);
        verify(userDetailsManager).loadUserByUsername(email);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenInvalidToken_whenDoFilterInternal_thenDoNotAuthenticate() throws ServletException, IOException {

        // Given
        String token = "invalid-jwt-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/some-path");
        when(jwtService.extractUsername(token)).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService).extractUsername(token);
        verify(jwtService, never()).isTokenValid(any(), any());
        verify(userDetailsManager, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenNoToken_whenDoFilterInternal_thenDoNotAuthenticate() throws ServletException, IOException {

        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getServletPath()).thenReturn("/some-path");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractUsername(any());
        verify(jwtService, never()).isTokenValid(any(), any());
        verify(userDetailsManager, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenAuthPath_whenDoFilterInternal_thenDoNotAuthenticate() throws ServletException, IOException {

        // Given
        when(request.getServletPath()).thenReturn("/auth/login");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractUsername(any());
        verify(jwtService, never()).isTokenValid(any(), any());
        verify(userDetailsManager, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }
}
