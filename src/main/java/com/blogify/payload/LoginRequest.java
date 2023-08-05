package com.blogify.payload;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequest {

    private String email;
    private String password;

}
