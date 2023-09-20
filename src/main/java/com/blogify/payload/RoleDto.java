package com.blogify.payload;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Name of the role", example = "ADMIN", required = true)
    @NotNull(message = "Role name cannot be null")
    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 255, message = "Role name cannot be longer than 255 characters")
    private String name;

    @Schema(description = "Description of the role", example = "Administrator role with full access", required = true)
    @NotNull(message = "Role description cannot be null")
    @NotBlank(message = "Role description cannot be blank")
    @Size(max = 255, message = "Role description cannot be longer than 255 characters")
    private String description;

}
