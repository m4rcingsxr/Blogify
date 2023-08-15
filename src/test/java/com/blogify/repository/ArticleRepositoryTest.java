package com.blogify.repository;

import com.blogify.entity.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "classpath:sql/articles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void givenArticles_whenFindById_thenReturnArticle() {
        // Given
        Long articleId = 1L;

        // When
        Optional<Article> foundArticle = articleRepository.findById(articleId);

        // Then
        assertTrue(foundArticle.isPresent());
        assertEquals("Introduction to Java", foundArticle.get().getTitle());
    }

    @Test
    void givenNewArticle_whenSave_thenArticleIsSaved() {
        // Given
        Article newArticle = new Article();
        newArticle.setTitle("New Article");
        newArticle.setDescription("Description of new article");
        newArticle.setContent("Content of new article");

        // When
        Article savedArticle = articleRepository.save(newArticle);

        // Then
        assertNotNull(savedArticle);
        assertNotNull(savedArticle.getId());
        assertEquals("New Article", savedArticle.getTitle());
    }

    @Test
    void givenExistingArticle_whenUpdate_thenArticleIsUpdated() {

        // Given
        Long articleId = 1L;
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        assertTrue(optionalArticle.isPresent());
        Article articleToUpdate = optionalArticle.get();
        articleToUpdate.setTitle("Updated Title");

        // When
        Article updatedArticle = articleRepository.save(articleToUpdate);

        // Then
        assertNotNull(updatedArticle);
        assertEquals("Updated Title", updatedArticle.getTitle());
    }

    @Test
    void givenArticleId_whenDelete_thenArticleIsDeleted() {
        // Given
        Long articleId = 1L;
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        assertTrue(optionalArticle.isPresent());

        // When
        articleRepository.deleteById(articleId);
        Optional<Article> deletedArticle = articleRepository.findById(articleId);

        // Then
        assertFalse(deletedArticle.isPresent());
    }

    @Test
    void givenArticles_whenFindAll_thenReturnAllArticles() {
        // When
        Iterable<Article> allArticles = articleRepository.findAll();

        // Then
        assertNotNull(allArticles);
        assertEquals(10, ((Collection<?>) allArticles).size());
    }
}
