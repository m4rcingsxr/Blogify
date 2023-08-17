package com.blogify.controller;

import com.blogify.payload.CategoryDto;
import com.blogify.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class CategoryControllerTest {

    private static final String BASE_URL = "/api/categories";
    private static final long CATEGORY_ID = 1L;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto();
        categoryDto.setId(CATEGORY_ID);
        categoryDto.setName("Test Category");
    }

    @Test
    @WithMockUser
    void whenListAll_thenReturnListOfCategories() throws Exception {
        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(2L);
        categoryDto2.setName("Test Category 2");

        when(categoryService.findAll()).thenReturn(List.of(categoryDto, categoryDto2));

        mockMvc.perform(get(BASE_URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value(categoryDto.getName()));

        verify(categoryService, times(1)).findAll();
    }

    @Test
    @WithMockUser
    void givenCategoryId_whenGetById_thenCategoryAndStatus200IsReturned() throws Exception {
        when(categoryService.findById(CATEGORY_ID)).thenReturn(categoryDto);

        mockMvc.perform(get(BASE_URL + "/" + CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));

        verify(categoryService, times(1)).findById(CATEGORY_ID);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenCategory_whenCreateCategory_thenCategoryIsCreated() throws Exception {
        when(categoryService.create(any(CategoryDto.class))).thenReturn(categoryDto);

        mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));

        verify(categoryService, times(1)).create(any(CategoryDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenCategoryId_whenUpdateCategory_thenCategoryUpdated() throws Exception {
        when(categoryService.update(CATEGORY_ID, categoryDto)).thenReturn(categoryDto);

        mockMvc.perform(put(BASE_URL + "/" + CATEGORY_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(categoryDto.getName()));

        verify(categoryService, times(1)).update(CATEGORY_ID, categoryDto);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenInvalidCategory_whenCreateOrUpdateCategory_thenReturnsValidationErrors() throws Exception {
        // Given an invalid CategoryDto
        CategoryDto invalidCategoryDto = new CategoryDto();
        invalidCategoryDto.setName("");

        // When & Then
        mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidCategoryDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Category name cannot be blank"));

        mockMvc.perform(put(BASE_URL + "/" + CATEGORY_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidCategoryDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Category name cannot be blank"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenCategoryId_whenDeleteCategory_thenCategoryIsDeleted() throws Exception {
        doNothing().when(categoryService).deleteById(CATEGORY_ID);

        mockMvc.perform(delete(BASE_URL + "/" + CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(categoryService, times(1)).deleteById(CATEGORY_ID);
    }
}
