package com.blogify;

import com.blogify.entity.Customer;
import com.blogify.entity.Role;
import com.blogify.payload.CustomerDto;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;

import java.util.Random;
import java.util.Set;

@UtilityClass
public class CustomerTestUtil {

    private static final Random random = new Random();
    private static final ModelMapper modelMapper = new ModelMapper();

    public Customer toEntity(CustomerDto customerDto) {
        return modelMapper.map(customerDto, Customer.class);
    }

    public CustomerDto toDto(Customer customer) {
        return modelMapper.map(customer, CustomerDto.class);
    }

    public static Customer generateDummyCustomer() {
        return new Customer(null, "john" + random.nextInt() + "@gmail.com", "plain123", "Doe", "abc", Set.of(generateAdminRole(), generateUserRole()));
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
