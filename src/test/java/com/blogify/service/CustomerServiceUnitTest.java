package com.blogify.service;

import com.blogify.CustomerTestUtil;
import com.blogify.entity.Customer;
import com.blogify.exception.ApiException;
import com.blogify.payload.CustomerDto;
import com.blogify.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceUnitTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

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

        CustomerDto customerDto = CustomerTestUtil.toDto(customer);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

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

    @Test
    void givenExistingWithPasswordCustomer_whenSaveCustomer_thenCustomerIsUpdatedWithNewBcryptPassword() {
        Customer existingCustomer = CustomerTestUtil.generateDummyCustomer();
        existingCustomer.setId(1L);

        Customer newCustomer = CustomerTestUtil.generateDummyCustomer();
        newCustomer.setId(1L);

        String rawPassword = newCustomer.getPassword();
        CustomerDto customerDto = CustomerTestUtil.toDto(newCustomer);

        when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.of(existingCustomer));
        when(passwordEncoder.encode(newCustomer.getPassword())).thenReturn("{bcrypt}" +rawPassword);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(newCustomer);

        customerService.update(existingCustomer.getId(), customerDto);

        assertTrue(newCustomer.getPassword().startsWith("{bcrypt}"));

        verify(customerRepository, times(1)).findById(existingCustomer.getId());
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(customerRepository, times(1)).save(newCustomer);
    }

    @Test
    void givenExistingWithoutPasswordCustomer_whenSaveCustomer_thenCustomerIsUpdatedWithOldPassword() {
        Customer existingCustomer = CustomerTestUtil.generateDummyCustomer();
        existingCustomer.setPassword("existingPassword");
        existingCustomer.setId(1L);

        Customer newCustomer = CustomerTestUtil.generateDummyCustomer();
        existingCustomer.setPassword(null);
        newCustomer.setId(1L);

        CustomerDto customerDto = CustomerTestUtil.toDto(newCustomer);

        when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.of(existingCustomer));
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(newCustomer);

        customerService.update(existingCustomer.getId(), customerDto);

        assertEquals(existingCustomer.getPassword(), newCustomer.getPassword());

        verify(customerRepository, times(1)).findById(existingCustomer.getId());
        verify(customerRepository, times(1)).save(newCustomer);
    }

    @Test
    void givenNotExistingCustomer_whenUpdate_thenShouldThrowApiException() {
        Customer notExistingCustomer = CustomerTestUtil.generateDummyCustomer();
        notExistingCustomer.setId(-1L);

        when(customerRepository.findById(notExistingCustomer.getId())).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> customerService.update(notExistingCustomer.getId(), CustomerTestUtil.toDto(notExistingCustomer)));

        verify(customerRepository, times(1)).findById(notExistingCustomer.getId());
        verifyNoInteractions(passwordEncoder);
    }

}