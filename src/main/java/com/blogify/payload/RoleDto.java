package com.blogify.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleDto extends EntityDto {

    @NotNull(message = "Role name cannot be null")
    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 255, message = "Role name cannot be longer than 255 characters")
    private String name;

    @NotNull(message = "Role description cannot be null")
    @NotBlank(message = "Role description cannot be blank")
    @Size(max = 255, message = "Role description cannot be longer than 255 characters")
    private String description;

}