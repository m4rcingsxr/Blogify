package com.blogify.payload;

import com.blogify.entity.Customer;
import lombok.*;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CustomerDto implements Serializable {
    Long id;
    String email;
    String password;
    String firstName;
    String lastName;
    Set<RoleDto> roles;
}