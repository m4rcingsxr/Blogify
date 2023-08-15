package com.blogify.service;

import com.blogify.entity.Article;
import com.blogify.exception.ApiException;
import com.blogify.payload.ArticleDto;
import com.blogify.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ArticleService articleService;

    private ArticleDto articleDto;
    private Article article;

    @BeforeEach
    void setUp() {
        articleDto = new ArticleDto();
        articleDto.setId(1L);
        articleDto.setTitle("Test Title");
        articleDto.setContent("Test Content");

        article = new Article();
        article.setId(1L);
        article.setTitle("Test Title");
        article.setContent("Test Content");
    }

    @Test
    void givenNewArticle_whenCreate_thenArticleIsCreated() {
        // Arrange
        when(articleRepository.save(any(Article.class))).thenReturn(article);
        when(modelMapper.map(any(ArticleDto.class), eq(Article.class))).thenReturn(article);

        // Act
        ArticleDto createdArticle = articleService.create(articleDto);

        // Assert
        assertNotNull(createdArticle);
        assertEquals(1L, createdArticle.getId());
        verify(articleRepository, times(1)).save(any(Article.class));
        verify(modelMapper, times(1)).map(any(ArticleDto.class), eq(Article.class));
    }

    @Test
    void givenExistingArticleId_whenUpdate_thenArticleIsUpdated() {
        // Arrange
        when(articleRepository.existsById(1L)).thenReturn(true);
        when(articleRepository.save(any(Article.class))).thenReturn(article);
        when(modelMapper.map(any(ArticleDto.class), eq(Article.class))).thenReturn(article);

        // Act
        ArticleDto updatedArticle = articleService.update(1L, articleDto);

        // Assert
        assertNotNull(updatedArticle);
        assertEquals(1L, updatedArticle.getId());
        verify(articleRepository, times(1)).existsById(1L);
        verify(articleRepository, times(1)).save(any(Article.class));
        verify(modelMapper, times(1)).map(any(ArticleDto.class), eq(Article.class));
    }

    @Test
    void givenNonExistingArticleId_whenUpdate_thenThrowsException() {
        // Arrange
        when(articleRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> articleService.update(1L, articleDto));
        assertEquals("Article not found", exception.getMessage());
        verify(articleRepository, times(1)).existsById(1L);
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void givenArticlesExist_whenFindAll_thenArticlesAreReturned() {
        // Arrange
        when(articleRepository.findAll()).thenReturn(Arrays.asList(article));
        when(modelMapper.map(any(Article.class), eq(ArticleDto.class))).thenReturn(articleDto);

        // Act
        List<ArticleDto> articles = articleService.findAll();

        // Assert
        assertNotNull(articles);
        assertEquals(1, articles.size());
        verify(articleRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(any(Article.class), eq(ArticleDto.class));
    }

    @Test
    void givenExistingArticleId_whenFindById_thenArticleIsReturned() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(modelMapper.map(any(Article.class), eq(ArticleDto.class))).thenReturn(articleDto);

        // Act
        ArticleDto foundArticle = articleService.findById(1L);

        // Assert
        assertNotNull(foundArticle);
        assertEquals(1L, foundArticle.getId());
        verify(articleRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(any(Article.class), eq(ArticleDto.class));
    }

    @Test
    void givenNonExistingArticleId_whenFindById_thenThrowsException() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> articleService.findById(1L));
        assertEquals("Article not found", exception.getMessage());
        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    void givenExistingArticleId_whenDeleteById_thenArticleIsDeleted() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        // Act
        articleService.deleteById(1L);

        // Assert
        verify(articleRepository, times(1)).findById(1L);
        verify(articleRepository, times(1)).delete(any(Article.class));
    }

    @Test
    void givenNonExistingArticleId_whenDeleteById_thenThrowsException() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> articleService.deleteById(1L));
        assertEquals("Article not found", exception.getMessage());
        verify(articleRepository, times(1)).findById(1L);
        verify(articleRepository, never()).delete(any(Article.class));
    }
}
