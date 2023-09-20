package com.blogify.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "HTTP status code of the error",
            example = "400",
            nullable = false)
    private Integer status;

    @Schema(description = "Error message describing the reason for the error",
            example = "Bad Request",
            nullable = false)
    private String message;

    @Schema(description = "Timestamp when the error occurred",
            example = "1625161237",
            nullable = false)
    private Long timestamp;

}