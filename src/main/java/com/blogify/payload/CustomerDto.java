package com.blogify.payload;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerDto extends EntityDto {

    @Email(message = "Email must have correct format")
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 255, message = "Email cannot be longer than 255 characters")
    String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min=8, max = 64, message = "Password must have min 8 characters and cannot be longer than 64 characters")
    String password;

    @NotNull(message = "First name cannot be null")
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 255, message = "First name cannot be longer than 255 characters")
    String firstName;

    @NotNull(message = "Last name cannot be null")
    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 255, message = "Last name cannot be longer than 255 characters")
    String lastName;

    @NotEmpty(message = "Roles cannot be empty")
    Set<RoleDto> roles;

}