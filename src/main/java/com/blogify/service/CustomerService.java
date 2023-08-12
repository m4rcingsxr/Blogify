package com.blogify.service;

import com.blogify.exception.ApiException;
import com.blogify.payload.CustomerDto;
import com.blogify.repository.CustomerRepository;
import com.blogify.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream().map(this::mapToDto).toList();
    }

    public void deleteCustomer(Long customerId) {
        Customer customer = findByIdInternal(customerId);
        customerRepository.delete(customer);
    }

    public CustomerDto update(Long customerId, CustomerDto customerDto) {
        Customer existingCustomer = findByIdInternal(customerId);
        Customer newCustomer = mapToEntity(customerDto);

        if(newCustomer.getPassword() != null) {
            newCustomer.setPassword(passwordEncoder.encode(newCustomer.getPassword()));
        } else {
            newCustomer.setPassword(existingCustomer.getPassword());
        }

        return mapToDto(customerRepository.save(newCustomer));
    }

    public CustomerDto findById(Long customerId) {
        Customer customer = findByIdInternal(customerId);
        return mapToDto(customer);
    }

    private ApiException generateCustomerNotFound() {
        return new ApiException(HttpStatus.NOT_FOUND, "Customer not found");
    }

    private CustomerDto mapToDto(Customer customer) {
        return modelMapper.map(customer, CustomerDto.class);
    }

    private Customer mapToEntity(CustomerDto customerDto) {
        return modelMapper.map(customerDto, Customer.class);
    }

    private Customer findByIdInternal(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(
                this::generateCustomerNotFound);
    }
}


