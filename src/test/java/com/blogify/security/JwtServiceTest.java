package com.blogify.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String JWT_SECRET = "daf66e01593f61a15b857cf433aae03a005812b31234e149036bcc8dee755dbb";
    private static final long JWT_EXPIRATION_DATE = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationDate", JWT_EXPIRATION_DATE);
    }

    @Test
    void givenUserDetails_whenGenerateToken_thenShouldGenerateCorrectToken() {

        // Given
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        User userDetails = new User("testuser", "", authorities);
        Date now = new Date();

        // When
        String token = jwtService.generateToken(Map.of(), userDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("testuser", claims.getSubject());
        assertEquals(List.of("ROLE_USER"), claims.get("authorities"));

        Date expirationDate = claims.getExpiration();
        assertNotNull(expirationDate);

        // Ensure that the expiration date is approximately 1 hour from the current time
        long expectedExpirationTime = now.getTime() + JWT_EXPIRATION_DATE;
        long actualExpirationTime = expirationDate.getTime();
        long tolerance = 1000; // 1 second tolerance

        assertTrue(Math.abs(expectedExpirationTime - actualExpirationTime) < tolerance);
    }

    @Test
    void givenJWTToken_whenGetUsername_thenShouldExtractUsername() {

        // Given
        String token = createTestToken("testuser", List.of("ROLE_USER"), false);

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void givenValidJWTToken_whenValidateToken_thenSignaturesMatch() {

        // Given
        String token = createTestToken("testuser", List.of("ROLE_USER"), false);
        User userDetails = new User("testuser", "", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    private String createTestToken(String username, List<String> roles, boolean expired) {

        Date currentDate = new Date();

        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION_DATE);

        if (expired) {
            expireDate = new Date(currentDate.getTime() - JWT_EXPIRATION_DATE);
        }

        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", roles)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    }
}
