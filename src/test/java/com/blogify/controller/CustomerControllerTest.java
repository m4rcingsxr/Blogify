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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

        customer1.setId(1);
        customer2.setId(2);

        when(customerService.findAll()).thenReturn(List.of(customer1, customer2));

        mockMvc.perform(get("/api/customers"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", Matchers.equalTo(customer1.getEmail())))
                .andExpect(jsonPath("$[0].firstName", Matchers.equalTo(customer1.getFirstName())))
                .andExpect(jsonPath("$[0].lastName", Matchers.equalTo(customer1.getLastName())))
                .andExpect(jsonPath("$[0].password", Matchers.equalTo(customer1.getPassword())));

        verify(customerService, times(1)).findAll();
    }

}