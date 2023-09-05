package com.blogify.payload;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JWTResponse {

    private String accessToken;
    private String tokenType = "Bearer";

}