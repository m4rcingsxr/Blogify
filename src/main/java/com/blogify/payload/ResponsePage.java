package com.blogify.payload;

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

    private Integer page;

    private Integer pageSize;

    private Long totalElements;

    private Integer totalPages;

    private List<T> content;

}
