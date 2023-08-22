package com.blogify.repository;

import com.blogify.entity.Customer;
import com.blogify.util.CustomerTestUtil;
import com.blogify.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static com.blogify.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = {
        "classpath:sql/customers.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void givenMultipleSortOrders_whenFindAll_thenShouldReturnSortedPageOfCustomers() {
        Sort a = getSortByMultipleFields(Sort.Direction.ASC, "firstName", "lastName");
        Sort b = getSort("email", Sort.Direction.DESC);
        Sort sort = getJoinedSort(a, b);

        PageRequest pageRequest = getPageRequest(0, sort);

        Page<Customer> customers = customerRepository.findAll(pageRequest);

        assertNotNull(customers);
        assertFalse(customers.getContent().isEmpty());
        assertEquals(10, customers.getTotalElements());
        assertEquals(2, customers.getTotalPages());
        assertTrue(TestUtil.isPageSortedCorrectly(customers, sort));
    }

    @Test
     void givenNoOrders_whenFindAll_thenShouldReturnUnsortedPageOfCustomers() {
        PageRequest pageRequest = getPageRequest(0, Sort.unsorted());

        Page<Customer> customers = customerRepository.findAll(pageRequest);

        assertNotNull(customers);
        assertFalse(customers.getContent().isEmpty());
        assertEquals(10, customers.getTotalElements());
        assertEquals(2, customers.getTotalPages());
    }

    @Test
    void givenExceedingPageNumber_whenFindAll_thenShouldReturnEmptyContent() {
        PageRequest pageRequest = getPageRequest(9, Sort.unsorted());

        Page<Customer> customers = customerRepository.findAll(pageRequest);

        assertNotNull(customers);
        assertTrue(customers.getContent().isEmpty());
        assertEquals(10, customers.getTotalElements());
        assertEquals(2, customers.getTotalPages());
    }

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
    void givenExistingEmail_whenFindByEmail_thenCustomerIsPresent() {

        Optional<Customer> customer = customerRepository.findByEmail("adam.adams@example.com");

        assertTrue(customer.isPresent());
    }

    @Test
    void givenNotExistingEmail_whenFindByEmail_thenCustomerIsNotPresent() {

        Optional<Customer> customer = customerRepository.findByEmail("not.exisiting@example.com");

        assertTrue(customer.isEmpty());
    }

    @Test
    void givenCustomerId_whenFindById_thenCustomerFound() {
        long customerId = 4L;

        Optional<Customer> byId = customerRepository.findById(customerId);

        assertTrue(byId.isPresent());
        assertEquals(customerId, byId.get().getId());
    }

    @Test
    void givenNotExistingCustomerId_whenFindById_thenCustomerFound() {
        long customerId = -1L;

        Optional<Customer> customer = customerRepository.findById(customerId);

        assertTrue(customer.isEmpty());
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
