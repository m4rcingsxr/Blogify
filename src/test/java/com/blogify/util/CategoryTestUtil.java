package com.blogify.util;

import com.blogify.entity.Category;
import com.blogify.payload.CategoryDto;

import java.util.Collections;

public class CategoryTestUtil {

    public static Category generateDummyCategory() {
        Category category = new Category();
        category.setName("Dummy Category");
        category.setArticles(Collections.emptyList());
        return category;
    }

    public static CategoryDto toDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setArticles(Collections.emptyList());
        return dto;
    }
}