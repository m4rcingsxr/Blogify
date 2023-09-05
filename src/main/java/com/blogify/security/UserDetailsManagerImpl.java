package com.blogify.security;

import com.blogify.entity.Customer;
import com.blogify.exception.ApiException;
import com.blogify.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserDetailsManagerImpl implements UserDetailsManager {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserDetails user) {
        Customer customer = new Customer();
        customer.setEmail(user.getUsername());
        customer.setPassword(passwordEncoder.encode(user.getPassword()));
        customerRepository.save(customer);
    }

    @Override
    public void updateUser(UserDetails user) {
        Customer customer = customerRepository.findByEmail(user.getUsername()).orElseThrow(
                this::generateNotFoundException);
        customer.setPassword(passwordEncoder.encode(user.getPassword()));
        customerRepository.save(customer);
    }

    @Override
    public void deleteUser(String username) {
        Customer customer = customerRepository.findByEmail(username).orElseThrow(
                this::generateNotFoundException);

        customerRepository.delete(customer);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerRepository.findByEmail(username).orElseThrow(
                this::generateNotFoundException);

        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        customer.setPassword(passwordEncoder.encode(newPassword));

        customerRepository.save(customer);
    }

    @Override
    public boolean userExists(String username) {
        return customerRepository.findByEmail(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) { // return 401 on exception
        Customer customer = customerRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));

        List<SimpleGrantedAuthority> authorities = customer.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        return new User(customer.getEmail(), customer.getPassword(), authorities);
    }

    private ApiException generateNotFoundException() {
        return new ApiException(HttpStatus.NOT_FOUND, "Customer not found");
    }
}
