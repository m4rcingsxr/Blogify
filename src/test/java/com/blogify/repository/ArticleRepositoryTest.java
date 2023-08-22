package com.blogify.repository;

import com.blogify.entity.Article;
import com.blogify.entity.Comment;
import com.blogify.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static com.blogify.util.ArticleTestUtil.generateDummyArticle;
import static com.blogify.util.CommentTestUtil.generateDummyComment;
import static com.blogify.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "classpath:sql/articles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void givenArticles_whenFindById_thenReturnArticle() {
        // Given
        Long articleId = 1L;

        // When
        Optional<Article> foundArticle = articleRepository.findById(articleId);

        // Then
        assertTrue(foundArticle.isPresent());
        assertEquals("Introduction to Java", foundArticle.get().getTitle());
        assertFalse(foundArticle.get().getComments().isEmpty());
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
        List<Article> allArticles = articleRepository.findAll();

        // Then
        assertNotNull(allArticles);
        assertEquals(10, allArticles.size());
    }

    @Test
    void givenMultipleSortOrders_whenFindAll_thenShouldReturnSortedPageOfArticles() {
        Sort a = getSortByMultipleFields(Sort.Direction.ASC, "id", "title");
        Sort b = getSort("description", Sort.Direction.DESC);
        Sort sort = getJoinedSort(a, b);

        PageRequest pageRequest = getPageRequest(0, sort);

        Page<Article> articles = articleRepository.findAll(pageRequest);

        assertNotNull(articles);
        assertFalse(articles.getContent().isEmpty());
        assertEquals(10, articles.getTotalElements());
        assertEquals(2, articles.getTotalPages());
        assertTrue(TestUtil.isPageSortedCorrectly(articles, sort));
    }

    @Test
    void givenNoOrders_whenFindAll_thenShouldReturnUnsortedPageOfArticles() {
        PageRequest pageRequest = getPageRequest(0, Sort.unsorted());

        Page<Article> articles = articleRepository.findAll(pageRequest);

        assertNotNull(articles);
        assertFalse(articles.getContent().isEmpty());
        assertEquals(10, articles.getTotalElements());
        assertEquals(2, articles.getTotalPages());
    }

    @Test
    void givenExceedingPageNumber_whenFindAll_thenShouldReturnEmptyContent() {
        PageRequest pageRequest = getPageRequest(9, Sort.unsorted());

        Page<Article> articles = articleRepository.findAll(pageRequest);

        assertNotNull(articles);
        assertTrue(articles.getContent().isEmpty());
        assertEquals(10, articles.getTotalElements());
        assertEquals(2, articles.getTotalPages());
    }

    @Test
        // cascade persist
    void givenArticleWithComments_whenSave_thenArticleAndCommentsAreSaved() {
        // Given
        Article article = generateDummyArticle();
        Comment comment = generateDummyComment();
        comment.setId(null);

        article.addComment(comment);

        // When
        Article savedArticle = articleRepository.save(article);

        // Then
        assertNotNull(savedArticle);
        assertNotNull(savedArticle.getId());
        assertEquals("Article with Comments", savedArticle.getTitle());
        assertEquals(1, savedArticle.getComments().size());
    }

    @Test
        // cascade delete
    void givenArticleWithComments_whenDelete_thenArticleAndCommentsAreDeleted() {

        // Given
        Long articleId = 1L;
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        assertTrue(optionalArticle.isPresent());

        Article article = optionalArticle.get();
        assertFalse(article.getComments().isEmpty());
        List<Long> commentsIds = article.getComments().stream().map(Comment::getId).toList();

        // When
        articleRepository.delete(article);
        Optional<Article> deletedArticle = articleRepository.findById(articleId);

        // Then
        assertFalse(deletedArticle.isPresent());

        List<Comment> comments = commentRepository.findAllById(commentsIds);
        assertTrue(comments.isEmpty());
    }
}
