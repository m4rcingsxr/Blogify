package com.blogify.service;

import com.blogify.exception.ApiException;
import com.blogify.repository.CustomerRepository;
import com.blogify.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public void deleteCustomer(Long customerId) {
        Customer customer = findById(customerId);

        customerRepository.delete(customer);
    }

    public Customer update(Long customerId, Customer customer) {
        Customer existingCustomer = findById(customerId);

        if(customer.getPassword() != null) {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        } else {
            customer.setPassword(existingCustomer.getPassword());
        }

        return customerRepository.save(customer);
    }

    public Customer findById(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(this::generateCustomerNotFound);
    }

    private ApiException generateCustomerNotFound() {
        return new ApiException(HttpStatus.NOT_FOUND, "Customer not found");
    }
}


