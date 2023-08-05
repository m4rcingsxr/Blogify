package com.blogify;

import com.blogify.entity.Customer;
import com.blogify.entity.Role;

import java.util.Random;
import java.util.Set;

public class CustomerTestUtil {

    private static final Random random = new Random();

    public static Customer generateDummyCustomer() {
        return new Customer(null, "john" + random.nextInt() + "@gmail.com", "plain123", "Doe", "abc", Set.of(generateAdminRole(), generateUserRole()));
    }

    public static Role generateAdminRole() {
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_ADMIN");
        return role;
    }

    public static Role generateUserRole() {
        Role role = new Role();
        role.setId(3);
        role.setName("ROLE_USER");
        return role;
    }

}
