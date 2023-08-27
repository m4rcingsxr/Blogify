package com.blogify.service;

import com.blogify.entity.Category;
import com.blogify.exception.ApiException;
import com.blogify.payload.CategoryDto;
import com.blogify.payload.ResponsePage;
import com.blogify.repository.CategoryRepository;
import com.blogify.util.CategoryTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceUnitTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void givenExistingCategory_whenDelete_thenRepositoryDeleteCategoryIsInvoked() {
        Category category = CategoryTestUtil.generateDummyCategory();
        category.setId(1L);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        categoryService.deleteById(category.getId());

        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void givenNotExistingCategory_whenDeleteCategory_thenApiExceptionIsThrown() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> categoryService.deleteById(-1L));

        verify(categoryRepository, times(1)).findById(-1L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void givenExistingCategory_whenFindById_thenFindCategoryIsInvoked() {
        Category category = CategoryTestUtil.generateDummyCategory();
        category.setId(1L);

        CategoryDto categoryDto = CategoryTestUtil.toDto(category);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        categoryService.findById(category.getId());

        verify(categoryRepository, times(1)).findById(category.getId());
    }

    @Test
    void givenNotExistingCategory_whenFindById_thenApiExceptionIsThrown() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> categoryService.findById(-1L));

        verify(categoryRepository, times(1)).findById(-1L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void givenValidCategory_whenCreate_thenCategoryIsSavedAndReturned() {
        Category category = CategoryTestUtil.generateDummyCategory();
        CategoryDto categoryDto = CategoryTestUtil.toDto(category);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(categoryDto, Category.class)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        CategoryDto result = categoryService.create(categoryDto);

        assertEquals(categoryDto, result);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void givenValidCategory_whenUpdate_thenCategoryIsUpdatedAndReturned() {
        Category category = CategoryTestUtil.generateDummyCategory();
        CategoryDto newCategory = CategoryTestUtil.toDto(category);

        when(categoryRepository.findByName(newCategory.getName())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(newCategory, Category.class)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(newCategory);

        CategoryDto result = categoryService.update(category.getId(), newCategory);

        assertEquals(newCategory, result);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void givenExistingCategoryName_whenValidateCategory_thenApiExceptionIsThrown() {
        Category category = CategoryTestUtil.generateDummyCategory();
        category.setId(1L);

        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));

        ApiException exception = assertThrows(ApiException.class, () -> categoryService.create(CategoryTestUtil.toDto(category)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Category name already in use", exception.getMessage());
    }

    @Test
    void givenNonExistingCategory_whenFindAll_thenReturnEmptyList() {
        // Arrange
        Page<Category> emptyPage = Page.empty(PageRequest.of(0, 10));
        when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        // Act
        ResponsePage<CategoryDto> result = categoryService.findAll(0, Sort.unsorted());

        // Assert
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getPage());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());

        verify(categoryRepository, times(1)).findAll(any(PageRequest.class));
    }


    @Test
    void givenExistingCategories_whenFindAll_thenReturnCategoryDtoList() {
        // Arrange
        Category category1 = CategoryTestUtil.generateDummyCategory();
        category1.setId(1L);
        Category category2 = CategoryTestUtil.generateDummyCategory();
        category2.setId(2L);
        Page<Category> categoryPage = new PageImpl<>(List.of(category1, category2), PageRequest.of(0, 1), 2);

        when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(categoryPage);
        when(modelMapper.map(category1, CategoryDto.class)).thenReturn(CategoryTestUtil.toDto(category1));
        when(modelMapper.map(category2, CategoryDto.class)).thenReturn(CategoryTestUtil.toDto(category2));

        // Act
        ResponsePage<CategoryDto> result = categoryService.findAll(0, Sort.unsorted());

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getTotalPages());

        verify(categoryRepository, times(1)).findAll(any(PageRequest.class));
    }

}
