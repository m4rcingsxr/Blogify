package com.blogify.controller;

import com.blogify.entity.Customer;
import com.blogify.payload.CustomerDto;
import com.blogify.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.List;

import static com.blogify.util.CustomerTestUtil.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class CustomerControllerTest {

    private static final String BASE_URL = "/api/customers";
    private static final long CUSTOMER_ID = 1L;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customer = generateDummyCustomer();
        customer.setId(CUSTOMER_ID);
        customerDto = toDto(customer);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenListAll_thenReturnListOfCustomers() throws Exception {
        Customer customer2 = generateDummyCustomer();
        customer2.setId(2L);

        when(customerService.findAll()).thenReturn(List.of(customerDto, toDto(customer2)));

        mockMvc.perform(get(BASE_URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").value(customer.getEmail()))
                .andExpect(jsonPath("$[0].firstName").value(customer.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(customer.getLastName()))
                .andExpect(jsonPath("$[0].password").value(customer.getPassword()));

        verify(customerService, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenCustomerId_whenDeleteCustomer_thenDeleteCustomerIsInvoked() throws Exception {
        doNothing().when(customerService).deleteById(CUSTOMER_ID);

        mockMvc.perform(delete(BASE_URL + "/" + CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(customerService, times(1)).deleteById(CUSTOMER_ID);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenCustomerId_whenGetById_thenCustomerAndStatus200IsReturned() throws Exception {
        when(customerService.findById(CUSTOMER_ID)).thenReturn(customerDto);

        mockMvc.perform(get(BASE_URL + "/" + CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(customer.getEmail()))
                .andExpect(jsonPath("$.firstName").value(customer.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(customer.getLastName()))
                .andExpect(jsonPath("$.password").value(customer.getPassword()))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles[*].name", containsInAnyOrder("ROLE_ADMIN", "ROLE_USER")));

        verify(customerService, times(1)).findById(CUSTOMER_ID);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenCustomer_whenUpdateCustomer_thenCustomerUpdated() throws Exception {
        when(customerService.update(CUSTOMER_ID, customerDto)).thenReturn(customerDto);

        mockMvc.perform(put(BASE_URL + "/" + CUSTOMER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(customer.getEmail()))
                .andExpect(jsonPath("$.firstName").value(customer.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(customer.getLastName()))
                .andExpect(jsonPath("$.password").value(customer.getPassword()))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles[*].name", containsInAnyOrder("ROLE_ADMIN", "ROLE_USER")));

        verify(customerService, times(1)).update(CUSTOMER_ID, customerDto);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenInvalidCustomer_whenUpdateCustomer_thenReturnsValidationErrors() throws Exception {
        // Given an invalid CustomerDto
        CustomerDto invalidCustomerDto = new CustomerDto();
        invalidCustomerDto.setEmail("invalid-email");
        invalidCustomerDto.setPassword("");
        invalidCustomerDto.setFirstName("");
        invalidCustomerDto.setLastName("");
        invalidCustomerDto.setRoles(new HashSet<>());

        // When & Then
        mockMvc.perform(put("/api/customers/{customerId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidCustomerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email must have correct format"))
                .andExpect(jsonPath("$.password").value("Password must have min 8 characters and cannot be longer than 64 characters"))
                .andExpect(jsonPath("$.firstName").value("First name cannot be blank"))
                .andExpect(jsonPath("$.lastName").value("Last name cannot be blank"))
                .andExpect(jsonPath("$.roles").value("Roles cannot be empty"));
    }
}
