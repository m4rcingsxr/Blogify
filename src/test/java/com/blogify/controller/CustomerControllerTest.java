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

import java.util.List;

import static com.blogify.CustomerTestUtil.*;
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
                .andExpect(jsonPath("$[0].email", equalTo(customer.getEmail())))
                .andExpect(jsonPath("$[0].firstName", equalTo(customer.getFirstName())))
                .andExpect(jsonPath("$[0].lastName", equalTo(customer.getLastName())))
                .andExpect(jsonPath("$[0].password", equalTo(customer.getPassword())));

        verify(customerService, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenCustomerId_whenDeleteCustomer_thenDeleteCustomerIsInvoked() throws Exception {
        doNothing().when(customerService).deleteCustomer(CUSTOMER_ID);

        mockMvc.perform(delete(BASE_URL + "/" + CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(customerService, times(1)).deleteCustomer(CUSTOMER_ID);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenCustomerId_whenGetById_thenCustomerAndStatus200IsReturned() throws Exception {
        when(customerService.findById(CUSTOMER_ID)).thenReturn(customerDto);

        mockMvc.perform(get(BASE_URL + "/" + CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", equalTo(customer.getEmail())))
                .andExpect(jsonPath("$.firstName", equalTo(customer.getFirstName())))
                .andExpect(jsonPath("$.lastName", equalTo(customer.getLastName())))
                .andExpect(jsonPath("$.password", equalTo(customer.getPassword())))
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
                .andExpect(jsonPath("$.email", equalTo(customer.getEmail())))
                .andExpect(jsonPath("$.firstName", equalTo(customer.getFirstName())))
                .andExpect(jsonPath("$.lastName", equalTo(customer.getLastName())))
                .andExpect(jsonPath("$.password", equalTo(customer.getPassword())))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles[*].name", containsInAnyOrder("ROLE_ADMIN", "ROLE_USER")));

        verify(customerService, times(1)).update(CUSTOMER_ID, customerDto);
    }
}
