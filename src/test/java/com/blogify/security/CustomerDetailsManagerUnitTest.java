package com.blogify.security;

import com.blogify.entity.Customer;
import com.blogify.exception.ApiException;
import com.blogify.repository.CustomerRepository;
import com.blogify.util.CustomerTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerDetailsManagerUnitTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CustomerDetailsManager customerDetailsManager;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = CustomerTestUtil.generateDummyCustomer();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void givenUserDetails_whenCreateUser_thenUserShouldBeSavedWithEncryptedPassword() {
        UserDetails userDetails = new User("email@gmail.com", "123", Collections.emptySet());

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("{bcrypt}123");

        customerDetailsManager.createUser(userDetails);

        verify(customerRepository).save(any(Customer.class));
        verify(passwordEncoder).encode(any(CharSequence.class));
    }

    @Test
    void givenUserDetails_whenUpdateUser_thenUserShouldBeUpdated() {
        UserDetails userDetails = new User(customer.getEmail(), customer.getPassword(), Collections.emptySet());

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("{bcrypt}123");
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        customerDetailsManager.updateUser(userDetails);

        verify(customerRepository).findByEmail(customer.getEmail());
        verify(customerRepository).save(any(Customer.class));
        verify(passwordEncoder).encode(any(CharSequence.class));
    }

    @Test
    void givenNotExistingEmail_whenUpdateUser_thenShouldThrowApiException() {
        UserDetails userDetails = new User(customer.getEmail(), customer.getPassword(), Collections.emptySet());

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> customerDetailsManager.updateUser(userDetails));

        verify(customerRepository).findByEmail(customer.getEmail());
        verifyNoMoreInteractions(customerRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void givenExistingEmail_whenDeleteUser_thenUserShouldBeDeleted() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        customerDetailsManager.deleteUser(customer.getEmail());

        verify(customerRepository).findByEmail(customer.getEmail());
        verify(customerRepository).delete(customer);
    }

    @Test
    void givenNotExistingEmail_whenDeleteUser_thenShouldThrowApiException() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> customerDetailsManager.deleteUser(customer.getEmail()));

        verify(customerRepository).findByEmail(customer.getEmail());
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void givenCorrectOldPassword_whenChangePassword_thenPasswordShouldBeChanged() {
        when(authentication.getName()).thenReturn(customer.getEmail());
        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("{bcrypt}newpassword");

        customerDetailsManager.changePassword("plain123", "newpassword");

        assertEquals("{bcrypt}newpassword", customer.getPassword());
        verify(customerRepository).save(customer);
    }

    @Test
    void givenIncorrectOldPassword_whenChangePassword_thenShouldThrowIllegalArgumentException() {
        when(authentication.getName()).thenReturn(customer.getEmail());
        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> customerDetailsManager.changePassword("oldpassword", "newpassword"));

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void givenExistingUser_whenUserExists_thenShouldReturnTrue() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        assertTrue(customerDetailsManager.userExists(customer.getEmail()));
    }

    @Test
    void givenNotExistingUser_whenUserExists_thenShouldReturnFalse() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.empty());

        assertFalse(customerDetailsManager.userExists(customer.getEmail()));
    }

    @Test
    void givenExistingUsername_whenLoadUserByUsername_thenShouldReturnUserDetails() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        UserDetails userDetails = customerDetailsManager.loadUserByUsername(customer.getEmail());

        assertNotNull(userDetails);
        assertEquals(customer.getEmail(), userDetails.getUsername());
    }

    @Test
    void givenNotExistingUsername_whenLoadUserByUsername_thenShouldThrowApiException() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> customerDetailsManager.loadUserByUsername(customer.getEmail()));
    }
}
