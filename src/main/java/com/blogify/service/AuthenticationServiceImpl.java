package com.blogify.service;

import com.blogify.entity.Customer;
import com.blogify.entity.Role;
import com.blogify.entity.Token;
import com.blogify.exception.ApiException;
import com.blogify.payload.JWTResponse;
import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;
import com.blogify.repository.CustomerRepository;
import com.blogify.repository.RoleRepository;
import com.blogify.repository.TokenRepository;
import com.blogify.security.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final SecureRandom secureRandom = new SecureRandom();

    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    @Value("${application.mailing.activation-url}")
    private String activationUrl;

    @Override
    public void register(RegistrationRequest registrationRequest) throws MessagingException {
        validateEmailInput(registrationRequest.getEmail());

        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(
                () -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                                       "Roles was not initialized correctly."
                ));
        encodePassword(registrationRequest);

        Customer customer = modelMapper.map(registrationRequest, Customer.class);
        customer.addRole(userRole);

        customerRepository.save(customer);

        sendValidationEmail(customer, generateAndSaveActivationToken(customer));
    }

    @Override
    @Transactional
    public void activate(String token) throws MessagingException {
        Token activationToken = tokenRepository.findByToken(token).orElseThrow(
                () -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid activation token."));
        if(LocalDateTime.now().isAfter(activationToken.getExpiresAt())) {
            sendValidationEmail(activationToken.getCustomer(), generateVerificationCode());
        }

        Customer customer = customerRepository.findById(activationToken.getCustomer().getId())
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
        customer.setEnabled(true);
        customerRepository.save(customer);

        tokenRepository.delete(activationToken);
    }

    @Override
    public JWTResponse login(LoginRequest loginRequest) {
        var authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getEmail(), loginRequest.getPassword()
                        )
                );

        var claims = new HashMap<String, Object>();
        var customer = (Customer) authentication.getPrincipal();

        claims.put("firstName", customer.getFirstName());
        claims.put("lastName", customer.getLastName());

        var token = jwtService.generateToken(claims, customer);
        return JWTResponse.builder()
                .accessToken(token)
                .build();
    }

    private void validateEmailInput(String email) {
        if (userDetailsManager.userExists(email)) {
            throw new ApiException(HttpStatus.CONFLICT,
                                   "Email is already connected with different account!."
            );
        }
    }

    private void encodePassword(RegistrationRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    private String generateAndSaveActivationToken(Customer customer) {
        String verificationToken = generateVerificationCode();

        Token token = Token.builder()
                .token(verificationToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .customer(customer)
                .build();

        tokenRepository.save(token);
        return verificationToken;
    }

    private void sendValidationEmail(Customer customer, String verificationCode)
            throws MessagingException {

        emailService.sendEmail(
                customer.getEmail(),
                customer.getFullName(),
                EmailService.TemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                verificationCode,
                "Account activation"
        );
    }

    private String generateVerificationCode() {
        return String.format("%06d", secureRandom.nextInt(1000000));
    }

}