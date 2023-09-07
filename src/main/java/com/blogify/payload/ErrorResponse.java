package com.blogify.payload;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private Integer status;
    private String message;
    private Long timestamp;

}
