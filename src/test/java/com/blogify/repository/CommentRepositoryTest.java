package com.blogify.repository;

import com.blogify.entity.Article;
import com.blogify.entity.Comment;
import com.blogify.util.CommentTestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = {
        "classpath:sql/articles.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void givenValidComment_whenSaveComment_thenCommentSaved() {
        // Given
        Comment newComment = CommentTestUtil.generateDummyComment("Barack Obama", "Great article!");
        Optional<Article> article = articleRepository.findById(1L);
        assertTrue(article.isPresent());
        newComment.setArticle(article.get());

        // When
        Comment savedComment = commentRepository.saveAndFlush(newComment);

        // Then
        assertNotNull(savedComment);
        assertNotNull(savedComment.getId());
        assertEquals("Barack Obama", savedComment.getFullName());
        assertEquals("Great article!", savedComment.getContent());
    }

    @Test
    void givenExistingCommentFullName_whenFindByFullName_thenCommentFound() {
        // When
        Optional<Comment> comment = commentRepository.findByFullName("John Doe");

        // Then
        assertTrue(comment.isPresent());
        assertEquals("John Doe", comment.get().getFullName());
    }

    @Test
    void givenNotExistingFullName_whenFindByFullName_thenCommentNotFound() {
        // When
        Optional<Comment> comment = commentRepository.findByFullName("Non Existent");

        // Then
        assertTrue(comment.isEmpty());
    }

    @Test
    void whenFindAll_thenShouldReturnAllComments() {
        // When
        List<Comment> comments = commentRepository.findAll();

        // Then
        assertNotNull(comments);
        assertEquals(10, comments.size());
    }

    @Test
    void givenCommentId_whenFindById_thenCommentFound() {
        // Given
        long commentId = 1L;

        // When
        Optional<Comment> comment = commentRepository.findById(commentId);

        // Then
        assertTrue(comment.isPresent());
        assertEquals(commentId, comment.get().getId());
    }

    @Test
    void givenNotExistingCommentId_whenFindById_thenCommentNotFound() {
        // Given
        long commentId = 11L;

        // When
        Optional<Comment> comment = commentRepository.findById(commentId);

        // Then
        assertTrue(comment.isEmpty());
    }

    @Test
    void givenCommentId_whenDeleteById_thenCommentDeleted() {
        // Given
        Optional<Comment> comment = commentRepository.findById(1L);
        assertTrue(comment.isPresent());

        // When
        commentRepository.delete(comment.get());
        Optional<Comment> deletedComment = commentRepository.findById(1L);

        // Then
        assertFalse(deletedComment.isPresent());
    }
}