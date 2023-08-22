package com.blogify.service;

import com.blogify.entity.Customer;
import com.blogify.exception.ApiException;
import com.blogify.payload.CustomerDto;
import com.blogify.payload.ResponsePage;
import com.blogify.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomerService implements EntityService<CustomerDto> {

    private static final int PAGE_SIZE = 10;

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public ResponsePage<CustomerDto> findAll(Integer pageNum, Sort sort) {
        Page<Customer> page = customerRepository.findAll(PageRequest.of(pageNum, PAGE_SIZE, sort));

        return ResponsePage.<CustomerDto>builder()
                .page(pageNum)
                .pageSize(PAGE_SIZE)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(page.getContent().stream().map(this::mapToDto).toList())
                .build();
    }

    @Override
    public void deleteById(Long customerId) {
        Customer customer = findByIdInternal(customerId);
        customerRepository.delete(customer);
    }

    @Override
    public CustomerDto update(Long customerId, CustomerDto customerDto) {
        validateEmail(customerId, customerDto.getEmail());

        customerDto.setId(customerId);

        Customer existingCustomer = findByIdInternal(customerId);
        Customer newCustomer = mapToEntity(customerDto);

        if (newCustomer.getPassword() != null) {
            newCustomer.setPassword(passwordEncoder.encode(newCustomer.getPassword()));
        } else {
            newCustomer.setPassword(existingCustomer.getPassword());
        }

        return mapToDto(customerRepository.save(newCustomer));
    }

    @Override
    public CustomerDto create(CustomerDto dto) {
        throw new ApiException(HttpStatus.METHOD_NOT_ALLOWED,
                               "Create method is not supported for customers. Creating of " +
                                       "customer can be achieved through Authorization endpoints"
        );
    }

    private void validateEmail(Long id, String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent() && !customer.get().getId().equals(id)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already in use");
        }
    }

    @Override
    public CustomerDto findById(Long customerId) {
        Customer customer = findByIdInternal(customerId);
        return mapToDto(customer);
    }

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(this::generateCustomerNotFound);
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


