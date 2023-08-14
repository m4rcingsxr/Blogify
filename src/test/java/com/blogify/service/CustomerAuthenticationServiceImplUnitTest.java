package com.blogify.service;

import com.blogify.entity.Customer;
import com.blogify.exception.ApiException;
import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;
import com.blogify.repository.CustomerRepository;
import com.blogify.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerAuthenticationServiceImplUnitTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsManager userDetailsManager;

    @InjectMocks
    private CustomerAuthenticationServiceImpl customerAuthenticationService;

    private LoginRequest loginRequest;
    private RegistrationRequest registrationRequest;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("email@example.com", "password");
        registrationRequest = new RegistrationRequest("John", "Doe", "email@example.com", "password");

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("email@example.com");
        customer.setPassword("encodedPassword");

        authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword(), Collections.emptyList());
    }

    @Test
    void givenValidLoginRequest_whenLogin_thenReturnJwtToken() {
        // Given
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
        given(jwtTokenProvider.generateToken(authentication)).willReturn("jwtToken");

        // When
        String token = customerAuthenticationService.login(loginRequest);

        // Then
        assertNotNull(token);
        assertEquals("jwtToken", token);
        then(authenticationManager).should().authenticate(any(UsernamePasswordAuthenticationToken.class));
        then(jwtTokenProvider).should().generateToken(authentication);
    }

    @Test
    void givenExistingEmail_whenRegister_thenThrowApiException() {
        // Given
        given(userDetailsManager.userExists(registrationRequest.getEmail())).willReturn(true);

        // When / Then
        ApiException exception = assertThrows(ApiException.class, () -> customerAuthenticationService.register(registrationRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Email is already connected with different account!.", exception.getMessage());
        then(userDetailsManager).should().userExists(registrationRequest.getEmail());
        then(customerRepository).shouldHaveNoInteractions();
    }

    @Test
    void givenValidRegistrationRequest_whenRegister_thenSaveCustomer() {
        // Given
        given(userDetailsManager.userExists(registrationRequest.getEmail())).willReturn(false);
        given(passwordEncoder.encode(registrationRequest.getPassword())).willReturn("encodedPassword");

        // When
        String result = customerAuthenticationService.register(registrationRequest);

        // Then
        assertEquals("User registered successfully!.", result);
        then(userDetailsManager).should().userExists(registrationRequest.getEmail());
        then(passwordEncoder).should().encode(registrationRequest.getPassword());
        then(customerRepository).should().save(any(Customer.class));
    }

    @Test
    void givenValidRegistrationRequest_whenRegister_thenCustomerShouldHaveRoles() {
        // Given
        given(userDetailsManager.userExists(registrationRequest.getEmail())).willReturn(false);
        given(passwordEncoder.encode(registrationRequest.getPassword())).willReturn("encodedPassword");

        // When
        customerAuthenticationService.register(registrationRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer savedCustomer = customerArgumentCaptor.getValue();
        assertEquals(1, savedCustomer.getRoles().size());
        assertTrue(savedCustomer.getRoles().stream().anyMatch(role -> role.getId() == 3));
    }
}
