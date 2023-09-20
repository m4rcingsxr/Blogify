package com.blogify.payload;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerDto extends EntityDto {

    @Schema(description = "Email of the customer",
            example = "customer@example.com",
            nullable = false,
            maxLength = 255)
    @Email(message = "Email must have correct format")
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 255, message = "Email cannot be longer than 255 characters")
    private String email;

    @Schema(description = "Password of the customer",
            example = "password123",
            minLength = 8,
            maxLength = 64)
    @Size(min = 8, max = 64, message = "Password must have min 8 characters and cannot be longer than 64 characters")
    private String password;

    @Schema(description = "First name of the customer",
            example = "John",
            nullable = false,
            maxLength = 64)
    @NotNull(message = "First name cannot be null")
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 64, message = "First name cannot be longer than 64 characters")
    private String firstName;

    @Schema(description = "Last name of the customer",
            example = "Doe",
            nullable = false,
            maxLength = 64)
    @NotNull(message = "Last name cannot be null")
    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 64, message = "Last name cannot be longer than 64 characters")
    private String lastName;

    @ArraySchema(schema = @Schema(description = "Roles assigned to the customer",
            nullable = false,
            implementation = RoleDto.class))
    @NotEmpty(message = "Roles cannot be empty")
    private Set<RoleDto> roles;

}