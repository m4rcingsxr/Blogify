package com.blogify.service;

import com.blogify.entity.Customer;
import com.blogify.entity.Role;
import com.blogify.entity.Token;
import com.blogify.exception.ApiException;
import com.blogify.payload.RegistrationRequest;
import com.blogify.repository.CustomerRepository;
import com.blogify.repository.RoleRepository;
import com.blogify.repository.TokenRepository;
import com.blogify.security.JwtService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

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
    private TokenRepository tokenRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegistrationRequest registrationRequest;
    private Customer customer;
    private Role userRole;
    private Token token;

    @BeforeEach
    void setUp() {
        registrationRequest = new RegistrationRequest("John", "Doe", "email@example.com", "password");

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("email@example.com");
        customer.setPassword("encodedPassword");

        userRole = new Role();
        userRole.setName("ROLE_USER");

        token = new Token();
        token.setToken("verificationCode");
        token.setCustomer(customer);
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
    void givenValidRegistrationRequest_whenRegister_thenSaveCustomerAndSendEmail() throws MessagingException {
        // Given
        given(userDetailsManager.userExists(registrationRequest.getEmail())).willReturn(false);
        given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.of(userRole));
        given(passwordEncoder.encode(registrationRequest.getPassword())).willReturn("encodedPassword");
        given(modelMapper.map(registrationRequest, Customer.class)).willReturn(customer);
        given(tokenRepository.save(any(Token.class))).willAnswer(invocation -> {
            Token savedToken = invocation.getArgument(0);
            savedToken.setToken("verificationCode");
            return savedToken;
        });

        // When
        authenticationService.register(registrationRequest);

        // Then
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        then(customerRepository).should().save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();
        assertEquals("encodedPassword", savedCustomer.getPassword());
        assertTrue(savedCustomer.getRoles().contains(userRole));

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> fullNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EmailService.TemplateName> templateCaptor = ArgumentCaptor.forClass(EmailService.TemplateName.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

        then(emailService).should().sendEmail(
                emailCaptor.capture(),
                fullNameCaptor.capture(),
                templateCaptor.capture(),
                urlCaptor.capture(),
                codeCaptor.capture(),
                subjectCaptor.capture()
        );

        assertEquals(customer.getEmail(), emailCaptor.getValue());
        assertEquals(customer.getFullName(), fullNameCaptor.getValue());
        assertEquals(EmailService.TemplateName.ACTIVATE_ACCOUNT, templateCaptor.getValue());
        assertNotNull(codeCaptor.getValue());
        assertEquals("Account activation", subjectCaptor.getValue());
    }


    @Test
    void givenNoUserRole_whenRegister_thenThrowApiException() {
        // Given
        given(userDetailsManager.userExists(registrationRequest.getEmail())).willReturn(false);
        given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.empty());

        // When / Then
        ApiException exception = assertThrows(ApiException.class, () -> authenticationService.register(registrationRequest));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertEquals("Roles was not initialized correctly.", exception.getMessage());
        then(roleRepository).should().findByName("ROLE_USER");
        then(customerRepository).shouldHaveNoInteractions();
    }
}
