package com.blogify.payload;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JWTResponse {
    private String accessToken;
    private String tokenType = "Bearer";
}