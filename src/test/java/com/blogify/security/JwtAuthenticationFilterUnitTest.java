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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterUnitTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

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
        String username = "testuser";
        Collection<GrantedAuthority> authorities = List.of();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUsername(token)).thenReturn(username);
        when(jwtTokenProvider.getAuthorities(token)).thenReturn(authorities);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(username, authentication.getPrincipal());
        assertEquals(authorities, authentication.getAuthorities());
        verify(jwtTokenProvider).validateToken(token);
        verify(jwtTokenProvider).getUsername(token);
        verify(jwtTokenProvider).getAuthorities(token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenInvalidToken_whenDoFilterInternal_thenDoNotAuthenticate() throws ServletException, IOException {

        // Given
        String token = "invalid-jwt-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenProvider).validateToken(token);
        verify(jwtTokenProvider, never()).getUsername(any());
        verify(jwtTokenProvider, never()).getAuthorities(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void givenNoToken_whenDoFilterInternal_thenDoNotAuthenticate() throws ServletException, IOException {

        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenProvider, never()).validateToken(any());
        verify(jwtTokenProvider, never()).getUsername(any());
        verify(jwtTokenProvider, never()).getAuthorities(any());
        verify(filterChain).doFilter(request, response);
    }
}
