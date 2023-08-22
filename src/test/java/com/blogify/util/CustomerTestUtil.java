package com.blogify.util;

import com.blogify.entity.Customer;
import com.blogify.entity.Role;
import com.blogify.payload.CustomerDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Random;
import java.util.Set;

@UtilityClass
public class CustomerTestUtil {

    private static final Random random = new Random();

    public static Customer generateDummyCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@doe.com");
        customer.setPassword("password");
        customer.setRoles(Set.of(generateUserRole(), generateAdminRole()));
        return customer;
    }

    public static Customer toEntity(CustomerDto customerDto) {
        return TestUtil.map(Customer.class, customerDto);
    }

    public static CustomerDto toDto(Customer customer) {
        return TestUtil.map(CustomerDto.class, customer);
    }

    public static CustomerDto generateCustomerDto() {
        return toDto(generateDummyCustomer());
    }

    public static Role generateAdminRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");
        return role;
    }

    public static Role generateUserRole() {
        Role role = new Role();
        role.setId(3L);
        role.setName("ROLE_USER");
        return role;
    }

}
