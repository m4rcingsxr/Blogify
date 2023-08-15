package com.blogify.controller;

import com.blogify.payload.ArticleDto;
import com.blogify.service.ArticleService;
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
class ArticleControllerTest {

    private static final String BASE_URL = "/api/articles";
    private static final long ARTICLE_ID = 1L;

    @MockBean
    private ArticleService articleService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ArticleDto articleDto;

    @BeforeEach
    void setUp() {
        articleDto = new ArticleDto();
        articleDto.setId(ARTICLE_ID);
        articleDto.setTitle("Test Title");
        articleDto.setDescription("Test Description");
        articleDto.setContent("Test Content");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenListAll_thenReturnListOfArticles() throws Exception {
        ArticleDto articleDto2 = new ArticleDto();
        articleDto2.setId(2L);
        articleDto2.setTitle("Test Title 2");
        articleDto2.setDescription("Test Description 2");
        articleDto2.setContent("Test Content 2");

        when(articleService.findAll()).thenReturn(List.of(articleDto, articleDto2));

        mockMvc.perform(get(BASE_URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value(articleDto.getTitle()))
                .andExpect(jsonPath("$[0].description").value(articleDto.getDescription()))
                .andExpect(jsonPath("$[0].content").value(articleDto.getContent()));

        verify(articleService, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenArticleId_whenGetById_thenArticleAndStatus200IsReturned() throws Exception {
        when(articleService.findById(ARTICLE_ID)).thenReturn(articleDto);

        mockMvc.perform(get(BASE_URL + "/" + ARTICLE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(articleDto.getTitle()))
                .andExpect(jsonPath("$.description").value(articleDto.getDescription()))
                .andExpect(jsonPath("$.content").value(articleDto.getContent()));

        verify(articleService, times(1)).findById(ARTICLE_ID);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenArticle_whenCreateArticle_thenArticleIsCreated() throws Exception {
        when(articleService.create(any(ArticleDto.class))).thenReturn(articleDto);

        mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(articleDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(articleDto.getTitle()))
                .andExpect(jsonPath("$.description").value(articleDto.getDescription()))
                .andExpect(jsonPath("$.content").value(articleDto.getContent()));

        verify(articleService, times(1)).create(any(ArticleDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenArticleId_whenUpdateArticle_thenArticleUpdated() throws Exception {
        when(articleService.update(ARTICLE_ID, articleDto)).thenReturn(articleDto);

        mockMvc.perform(put(BASE_URL + "/" + ARTICLE_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(articleDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(articleDto.getTitle()))
                .andExpect(jsonPath("$.description").value(articleDto.getDescription()))
                .andExpect(jsonPath("$.content").value(articleDto.getContent()));

        verify(articleService, times(1)).update(ARTICLE_ID, articleDto);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenInvalidArticle_whenCreateOrUpdateArticle_thenReturnsValidationErrors() throws Exception {
        // Given an invalid ArticleDto
        ArticleDto invalidArticleDto = new ArticleDto();
        invalidArticleDto.setTitle("");
        invalidArticleDto.setDescription("");
        invalidArticleDto.setContent("");

        // When & Then
        mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidArticleDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title cannot be blank"))
                .andExpect(jsonPath("$.description").value("Description cannot be blank"))
                .andExpect(jsonPath("$.content").value("Content cannot be blank"));

        mockMvc.perform(put(BASE_URL + "/" + ARTICLE_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidArticleDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title cannot be blank"))
                .andExpect(jsonPath("$.description").value("Description cannot be blank"))
                .andExpect(jsonPath("$.content").value("Content cannot be blank"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void givenArticleId_whenDeleteArticle_thenArticleIsDeleted() throws Exception {
        doNothing().when(articleService).deleteById(ARTICLE_ID);

        mockMvc.perform(delete(BASE_URL + "/" + ARTICLE_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(articleService, times(1)).deleteById(ARTICLE_ID);
    }
}
