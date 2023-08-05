package com.blogify;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Test
    void givenPlainPassword_whenEncode_thenEncodedPasswordShouldBeReturned() {
        String plainPassword = "plain123";

        String encodedPassword = passwordEncoder.encode(plainPassword);

        assertTrue(BCrypt.checkpw(plainPassword, encodedPassword));
    }

}
