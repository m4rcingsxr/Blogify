package com.blogify.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequest {

    @Schema(description = "Email address of the user", example = "user@example.com", nullable = false)
    @Email(message = "Email must have correct format")
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 255, message = "Email cannot be longer than 255 characters")
    private String email;

    @Schema(description = "Password of the user", example = "password123", nullable = false)
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    @Size(max = 64, message = "Password cannot be longer than 64 characters")
    private String password;

}
