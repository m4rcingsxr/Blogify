package com.blogify.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JWTResponse {

    @Schema(description = "Access token for authentication")
    private String accessToken;

    @Schema(description = "Type of the token")
    private String tokenType = "Bearer";

}
