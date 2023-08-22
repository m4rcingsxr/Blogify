package com.blogify.util;

import com.blogify.entity.Category;
import com.blogify.payload.CategoryDto;

import java.util.Collections;

public class CategoryTestUtil {

    public static Category generateDummyCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Dummy Category");
        category.setArticles(Collections.emptyList());
        return category;
    }

    public static CategoryDto generateDummyCategoryDto() {
        return toDto(generateDummyCategory());
    }

    public static CategoryDto toDto(Category category) {
        return TestUtil.map(CategoryDto.class, category);
    }

    public static Category toEntity(CategoryDto category) {
        return TestUtil.map(Category.class, category);
    }


}