package com.blogify.security;

import com.blogify.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


 class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String JWT_SECRET = "daf66e01593f61a15b857cf433aae03a005812b31234e149036bcc8dee755dbb";
    private static final long JWT_EXPIRATION_DATE = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationDate", JWT_EXPIRATION_DATE);
    }


     @Test
     void givenAuthentication_whenGenerateToken_thenShouldGenerateCorrectToken() {

         // Given
         List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
         TestingAuthenticationToken authentication = new TestingAuthenticationToken("testuser", null, authorities);
         Date now = new Date();

         // When
         String token = jwtTokenProvider.generateToken(authentication);

         // Then
         assertNotNull(token);
         assertFalse(token.isEmpty());

         Claims claims = Jwts.parser()
                 .verifyWith(getSigningKey())
                 .build()
                 .parseSignedClaims(token)
                 .getPayload();

         assertEquals("testuser", claims.getSubject());
         assertEquals("ROLE_USER", claims.get("roles"));

         Date expirationDate = claims.getExpiration();
         assertNotNull(expirationDate);

         // Ensure that the expiration date is approximately 1 hour from the current time
         long expectedExpirationTime = now.getTime() + (long) ReflectionTestUtils.getField(jwtTokenProvider, "jwtExpirationDate");
         long actualExpirationTime = expirationDate.getTime();
         long tolerance = 1000; // 1 second tolerance

         assertTrue(Math.abs(expectedExpirationTime - actualExpirationTime) < tolerance);
     }



     @Test
    void givenJWTToken_whenGetUsername_thenShouldExtractUsername() {

        // Given
        String token = createTestToken("testuser", "ROLE_USER", false);

        // When
        String username = jwtTokenProvider.getUsername(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void givenValidJWTToken_whenValidateToken_thenSignaturesMatch() {

        // Given
        String token = createTestToken("testuser", "ROLE_USER", false);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void givenInvalidJWTToken_whenValidateToken_thenSignaturesDoesNotMatch() {
        // Given
        String token = "invalid-token";

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> jwtTokenProvider.validateToken(token));
        assertEquals("Invalid JWT Token", exception.getMessage());
    }

    @Test
    void givenExpiredJWTToken_whenValidateToken_thenJWTTokenIsNotValid() {
        // Given
        String token = createTestToken("testuser", "ROLE_USER", true);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> jwtTokenProvider.validateToken(token));
        assertEquals("Expired JWT token", exception.getMessage());
    }

    @Test
    void givenValidTokeWithAuthorities_whenExtractAuthorities_thenShouldReturnCorrectAuthorities() {

        // Given
        String token = createTestToken("testuser", "ROLE_USER,ROLE_ADMIN", false);

        // When
        Collection<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);

        // Then
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    private String createTestToken(String username, String roles, boolean expired) {

        Date currentDate = new Date();

        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION_DATE);

        if(expired) {
            expireDate = new Date();
        }

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(getSigningKey())
                .compact();
    }

     private SecretKey getSigningKey() {
         return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
     }
}
