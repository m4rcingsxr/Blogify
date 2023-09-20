package com.blogify.payload;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ResponsePage<T> {

    @Schema(description = "Current page number", example = "1")
    private Integer page;

    @Schema(description = "Number of items per page", example = "10")
    private Integer pageSize;

    @Schema(description = "Total number of elements", example = "100")
    private Long totalElements;

    @Schema(description = "Total number of pages", example = "10")
    private Integer totalPages;

    @ArraySchema(schema = @Schema(description = "Content of the current page", implementation = Object.class))
    private List<T> content;

}
