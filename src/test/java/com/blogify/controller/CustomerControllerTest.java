package com.blogify.controller;

import com.blogify.entity.Customer;
import com.blogify.payload.CustomerDto;
import com.blogify.payload.ResponsePage;
import com.blogify.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.List;

import static com.blogify.util.CustomerTestUtil.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class CustomerControllerTest {

    private static final String BASE_URL = "/customers";
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
    @WithMockUser
    void whenFindAll_thenReturnListOfCustomers() throws Exception {
        CustomerDto customerDto2 = generateCustomerDto();

        ResponsePage<CustomerDto> responsePage = new ResponsePage<>();
        responsePage.setContent(List.of(customerDto, customerDto2));
        responsePage.setPage(0);
        responsePage.setPageSize(2);
        responsePage.setTotalElements(2L);
        responsePage.setTotalPages(1);

        when(customerService.findAll(anyInt(), any(Sort.class))).thenReturn(responsePage);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].firstName").value(customerDto.getFirstName()))
                .andExpect(jsonPath("$.content[0].lastName").value(customerDto.getLastName()))
                .andExpect(jsonPath("$.content[1].firstName").value(customerDto2.getFirstName()))
                .andExpect(jsonPath("$.content[1].lastName").value(customerDto2.getLastName()));

        verify(customerService, times(1)).findAll(anyInt(), any(Sort.class));
    }

    @Test
    @WithMockUser
    void whenFindAllWithPagination_thenReturnPaginatedListOfCustomers() throws Exception {
        ResponsePage<CustomerDto> responsePage = new ResponsePage<>();
        responsePage.setContent(List.of(customerDto));
        responsePage.setPage(1);
        responsePage.setPageSize(1);
        responsePage.setTotalElements(2L);
        responsePage.setTotalPages(2);

        when(customerService.findAll(anyInt(), any(Sort.class))).thenReturn(responsePage);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).param("page", "1").param("size", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName").value(customerDto.getFirstName()))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.pageSize").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));

        verify(customerService, times(1)).findAll(anyInt(), any(Sort.class));
    }

    @Test
    @WithMockUser
    void whenFindAllWithInvalidSort_thenReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL).param("sort", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenFindAllEmpty_thenReturnEmptyList() throws Exception {
        ResponsePage<CustomerDto> responsePage = new ResponsePage<>();
        responsePage.setContent(List.of());
        responsePage.setPage(0);
        responsePage.setPageSize(2);
        responsePage.setTotalElements(0L);
        responsePage.setTotalPages(0);

        when(customerService.findAll(anyInt(), any(Sort.class))).thenReturn(responsePage);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

        verify(customerService, times(1)).findAll(anyInt(), any(Sort.class));
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
        mockMvc.perform(put(BASE_URL + "/{customerId}", 1L)
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
