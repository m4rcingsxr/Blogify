package com.blogify.service;

import java.util.List;

public interface EntityService<R> {

    R create(R dto);

    R update(Long id, R dto);

    List<R> findAll();

    R findById(Long id);

    void deleteById(Long id);

}
