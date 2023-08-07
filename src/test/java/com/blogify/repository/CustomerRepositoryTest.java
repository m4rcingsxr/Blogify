package com.blogify.repository;

import com.blogify.CustomerTestUtil;
import com.blogify.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = {
        "classpath:sql/customers.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void givenValidCustomer_whenSaveCustomer_thenCustomerSaved() {
        int expectedRoles = 2;

        Customer customer = CustomerTestUtil.generateDummyCustomer();
                                                            
        Customer savedCustomer = customerRepository.save(customer);

        assertNotNull(savedCustomer);
        assertNotNull(savedCustomer.getId());
        assertEquals(expectedRoles, savedCustomer.getRoles().size());
    }

    @Test
    void givenNotExisting_whenFindByEmail_thenCustomerFound() {

        Optional<Customer> customer = customerRepository.findByEmail("john.doe@example.com");

        assertTrue(customer.isPresent());
        assertEquals("john.doe@example.com", customer.get().getEmail());
    }

    @Test
    void givenNotExistingEmail_whenFindByEmail_thenCustomerIsNotPresent() {

        Optional<Customer> customer = customerRepository.findByEmail("not.exisiting@example.com");

        assertTrue(customer.isEmpty());
    }

    @Test
    void whenFindAll_thenShouldReturnAllCustomers() {
        int expectedSize = 4;

        List<Customer> customers = customerRepository.findAll();

        assertEquals(expectedSize, customers.size());
    }

    @Test
    void givenCustomerId_whenDeleteById_thenCustomerDeleted() {
        Optional<Customer> customer = customerRepository.findById(1L);

        assertTrue(customer.isPresent());

        customerRepository.delete(customer.get());
        Optional<Customer> deletedCustomer = customerRepository.findById(1L);
        assertFalse(deletedCustomer.isPresent());
    }

}
