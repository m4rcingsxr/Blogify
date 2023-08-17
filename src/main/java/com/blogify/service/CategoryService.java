package com.blogify.service;

import com.blogify.entity.Category;
import com.blogify.exception.ApiException;
import com.blogify.payload.CategoryDto;
import com.blogify.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService implements EntityService<CategoryDto> {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        validateCategory(null, categoryDto.getName());
        Category savedCategory = categoryRepository.save(mapToEntity(categoryDto));

        return mapToDto(savedCategory);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        validateCategory(id, categoryDto.getName());
        categoryDto.setId(id);

        Category savedCategory = categoryRepository.save(mapToEntity(categoryDto));

        return mapToDto(savedCategory);
    }

    private void validateCategory(Long id, String name) {
        categoryRepository.findByName(name).ifPresent(category -> {
            if(!category.getId().equals(id)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Category name already in use");
            }
        });
    }

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Override
    public CategoryDto findById(Long id) {
        return mapToDto(findByIdInternal(id));
    }

    @Override
    public void deleteById(Long id) {
        Category category = findByIdInternal(id);
        categoryRepository.delete(category);
    }

    private Category findByIdInternal(Long id) {
        return categoryRepository.findById(id).orElseThrow(this::generateNotFound);
    }

    private CategoryDto mapToDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

    private Category mapToEntity(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }

    private ApiException generateNotFound() {
        return ApiException.notFound("Category not found");
    }
}
