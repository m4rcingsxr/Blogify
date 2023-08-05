package com.blogify.payload;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegistrationRequest {

    private String email;
    private String firstName;
    private String lastName;
    private String password;

}