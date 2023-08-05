package com.blogify.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Data
public class ErrorResponse {

    private Integer status;
    private String message;
    private Long timestamp;

}
