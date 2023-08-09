package com.blogify.controller;

import com.blogify.CustomerTestUtil;
import com.blogify.entity.Customer;
import com.blogify.service.CustomerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class CustomerControllerTest {

    @MockBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenListAll_thenReturnListOfCustomers() throws Exception {
        Customer customer1 = CustomerTestUtil.generateDummyCustomer();
        Customer customer2 = CustomerTestUtil.generateDummyCustomer();

        customer1.setId(1L);
        customer2.setId(2L);

        when(customerService.findAll()).thenReturn(List.of(customer1, customer2));

        mockMvc.perform(get("/api/customers"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", equalTo(customer1.getEmail())))
                .andExpect(jsonPath("$[0].firstName", equalTo(customer1.getFirstName())))
                .andExpect(jsonPath("$[0].lastName", equalTo(customer1.getLastName())))
                .andExpect(jsonPath("$[0].password", equalTo(customer1.getPassword())));

        verify(customerService, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenCustomerId_whenDeleteCustomer_thenDeleteCustomerIsInvoked() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(customerService, times(1)).deleteCustomer(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenCustomerId_whenGetById_thenCustomerAndStatus200IsReturned() throws Exception {
        long customerId = 1;

        Customer customer = CustomerTestUtil.generateDummyCustomer();
        customer.setId(customerId);

        when(customerService.findById(customerId)).thenReturn(customer);

        mockMvc.perform(get("/api/customers/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", equalTo(customer.getEmail())))
                .andExpect(jsonPath("$.firstName", equalTo(customer.getFirstName())))
                .andExpect(jsonPath("$.lastName", equalTo(customer.getLastName())))
                .andExpect(jsonPath("$.password", equalTo(customer.getPassword())))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles[*].name", containsInAnyOrder("ROLE_ADMIN", "ROLE_USER")));

        verify(customerService, times(1)).findById(customerId);
    }
}