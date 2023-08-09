package com.blogify.service;

import com.blogify.CustomerTestUtil;
import com.blogify.entity.Customer;
import com.blogify.exception.ApiException;
import com.blogify.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceUnitTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;


    @Test
    void givenExistingCustomer_whenDelete_thenRepositoryDeleteCustomerIsInvoked() {
        Customer customer = CustomerTestUtil.generateDummyCustomer();
        customer.setId(1L);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(customer.getId());

        verify(customerRepository, times(1)).findById(customer.getId());
        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    void givenNotExistingCustomer_whenDeleteCustomer_thenApiExceptionIsThrown() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> customerService.deleteCustomer(-1L));

        verify(customerRepository, times(1)).findById(-1L);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void givenExistingCustomer_whenFindById_thenFindCustomerIsInvoked() {
        Customer customer = CustomerTestUtil.generateDummyCustomer();
        customer.setId(1L);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        customerService.findById(customer.getId());

        verify(customerRepository, times(1)).findById(customer.getId());
    }

    @Test
    void givenNotExistingCustomer_whenFindById_thenApiExceptionIsThrown() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> customerService.findById(-1L));

        verify(customerRepository, times(1)).findById(-1L);
        verifyNoMoreInteractions(customerRepository);
    }



}