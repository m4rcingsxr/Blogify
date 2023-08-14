package com.blogify.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JWTResponse {

    private String accessToken;
    private String tokenType = "Bearer";

}