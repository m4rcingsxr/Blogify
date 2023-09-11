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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final SecureRandom secureRandom = new SecureRandom();

    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;


    @Override
    public void register(RegistrationRequest registrationRequest) {
        validateEmailInput(registrationRequest.getEmail());

        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(
                () -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                                       "Roles was not initialized correctly."
                ));
        encodePassword(registrationRequest);

        Customer customer = modelMapper.map(registrationRequest, Customer.class);
        customer.addRole(userRole);

        customerRepository.save(customer);
    }

    @Override
    public void activate(String token) {

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

    private void encodePassword(RegistrationRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    private void validateEmailInput(String email) {
        if (userDetailsManager.userExists(email)) {
                throw new ApiException(HttpStatus.CONFLICT, "Email is already connected with different account!."
            );
        }
    }

    private String generateVerificationCode() {
        return String.format("%06d", secureRandom.nextInt(1000000));
    }

}