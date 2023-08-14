package com.blogify.service;

import com.blogify.entity.Customer;
import com.blogify.entity.Role;
import com.blogify.exception.ApiException;
import com.blogify.payload.LoginRequest;
import com.blogify.payload.RegistrationRequest;
import com.blogify.repository.CustomerRepository;
import com.blogify.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CustomerAuthenticationServiceImpl implements CustomerAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsManager userDetailsManager;

    @Override
    public String login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public String register(RegistrationRequest registrationRequest) {

        if(userDetailsManager.userExists(registrationRequest.getEmail())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already connected with different account!.");
        }

        Customer customer = new Customer();
        customer.setFirstName(registrationRequest.getFirstName());
        customer.setLastName(registrationRequest.getLastName());
        customer.setEmail(registrationRequest.getEmail());
        customer.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role customerRole = new Role();
        customerRole.setId(3L);
        roles.add(customerRole);
        customer.setRoles(roles);

        customerRepository.save(customer);

        return "User registered successfully!.";
    }
}