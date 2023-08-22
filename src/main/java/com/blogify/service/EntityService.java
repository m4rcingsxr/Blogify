package com.blogify.service;

import com.blogify.payload.ResponsePage;
import org.springframework.data.domain.Sort;

public interface EntityService<R> {

    R create(R dto);

    R update(Long id, R dto);

    ResponsePage<R> findAll(Integer page, Sort sort);

    R findById(Long id);

    void deleteById(Long id);

}
