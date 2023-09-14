package com.blogify.service;

import com.blogify.entity.Customer;
import com.blogify.entity.Role;
import com.blogify.exception.ApiException;
import com.blogify.payload.JWTResponse;
import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;
import com.blogify.repository.CustomerRepository;
import com.blogify.repository.RoleRepository;
import com.blogify.security.JwtService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplUnitTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsManager userDetailsManager;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private LoginRequest loginRequest;
    private RegistrationRequest registrationRequest;
    private Customer customer;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("email@example.com", "password");
        registrationRequest = new RegistrationRequest("John", "Doe", "email@example.com", "password");

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("email@example.com");
        customer.setPassword("encodedPassword");
    }

    @Test
    void givenValidLoginRequest_whenLogin_thenReturnJwtToken() {
        // Given
        var authentication = new UsernamePasswordAuthenticationToken(customer, null, Collections.emptyList());
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
        given(jwtService.generateToken(any(Map.class), any(Customer.class))).willReturn("jwtToken");

        // When
        JWTResponse response = authenticationService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        then(authenticationManager).should().authenticate(any(UsernamePasswordAuthenticationToken.class));
        then(jwtService).should().generateToken(any(Map.class), any(Customer.class));
    }

    @Test
    void givenExistingEmail_whenRegister_thenThrowApiException() {
        // Given
        given(userDetailsManager.userExists(registrationRequest.getEmail())).willReturn(true);

        // When / Then
        ApiException exception = assertThrows(ApiException.class, () -> authenticationService.register(registrationRequest));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Email is already connected with different account!.", exception.getMessage());
        then(userDetailsManager).should().userExists(registrationRequest.getEmail());
        then(customerRepository).shouldHaveNoInteractions();
    }

    @Test
    void givenValidRegistrationRequest_whenRegister_thenSaveCustomer() throws MessagingException {
        // Given
        Role roleUser = new Role();
        roleUser.setId(3L);
        roleUser.setName("ROLE_USER");

        given(userDetailsManager.userExists(registrationRequest.getEmail())).willReturn(false);
        given(passwordEncoder.encode(registrationRequest.getPassword())).willReturn("encodedPassword");
        given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.of(roleUser));
        given(modelMapper.map(registrationRequest, Customer.class)).willReturn(customer);

        // When
        authenticationService.register(registrationRequest);

        // Then
        then(userDetailsManager).should().userExists(registrationRequest.getEmail());
        then(passwordEncoder).should().encode("password");
        then(customerRepository).should().save(any(Customer.class));
    }

    @Test
    void givenValidRegistrationRequest_whenRegister_thenCustomerShouldHaveRoles()
            throws MessagingException {

        // Given
        Role userRole = new Role();
        userRole.setId(3L);
        userRole.setName("ROLE_USER");

        given(userDetailsManager.userExists(registrationRequest.getEmail())).willReturn(false);
        given(passwordEncoder.encode(registrationRequest.getPassword())).willReturn("encodedPassword");
        given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.of(userRole));
        given(modelMapper.map(registrationRequest, Customer.class)).willReturn(customer);

        // When
        authenticationService.register(registrationRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer savedCustomer = customerArgumentCaptor.getValue();
        assertEquals(1, savedCustomer.getRoles().size());
        assertTrue(savedCustomer.getRoles().contains(userRole));
    }
}
