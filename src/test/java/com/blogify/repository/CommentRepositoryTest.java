package com.blogify.repository;

import com.blogify.entity.Article;
import com.blogify.entity.Comment;
import com.blogify.util.CommentTestUtil;
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

import static com.blogify.util.TestUtil.*;
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
        Comment newComment = CommentTestUtil.generateDummyComment();
        newComment.setId(null);
        Optional<Article> article = articleRepository.findById(1L);
        assertTrue(article.isPresent());
        newComment.setArticle(article.get());

        // When
        Comment savedComment = commentRepository.saveAndFlush(newComment);

        // Then
        assertNotNull(savedComment);
        assertNotNull(savedComment.getId());
    }

    @Test
    void givenExistingCommentFullNameAndArticleId_whenFindByFullNameAndArticleId_thenCommentFound() {
        // When
        Optional<Comment> comment = commentRepository.findByFullNameAndArticleId("John Doe", 1L);

        // Then
        assertTrue(comment.isPresent());
        assertEquals("John Doe", comment.get().getFullName());
        assertNotNull(comment.get().getArticle());
    }

    @Test
    void givenNotExistingFullNameWithArticleId_whenFindByFullName_thenCommentNotFound() {
        // When
        Optional<Comment> comment = commentRepository.findByFullNameAndArticleId("Non Existent", 1L);

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
    void givenMultipleSortOrders_whenFindAll_thenShouldReturnSortedPageOfComments() {
        Sort a = getSortByMultipleFields(Sort.Direction.ASC, "id", "fullName");
        Sort b = getSort("content", Sort.Direction.DESC);
        Sort sort = getJoinedSort(a, b);

        PageRequest pageRequest = getPageRequest(0, sort);

        Page<Comment> customers = commentRepository.findAll(pageRequest);

        assertNotNull(customers);
        assertFalse(customers.getContent().isEmpty());
        assertEquals(10, customers.getTotalElements());
        assertEquals(2, customers.getTotalPages());
        assertTrue(TestUtil.isPageSortedCorrectly(customers, sort));
    }

    @Test
    void givenNoOrders_whenFindAll_thenShouldReturnUnsortedPageOfComments() {
        PageRequest pageRequest = getPageRequest(0, Sort.unsorted());

        Page<Comment> customers = commentRepository.findAll(pageRequest);

        assertNotNull(customers);
        assertFalse(customers.getContent().isEmpty());
        assertEquals(10, customers.getTotalElements());
        assertEquals(2, customers.getTotalPages());
    }

    @Test
    void givenExceedingPageNumber_whenFindAll_thenShouldReturnEmptyContent() {
        PageRequest pageRequest = getPageRequest(9, Sort.unsorted());

        Page<Comment> customers = commentRepository.findAll(pageRequest);

        assertNotNull(customers);
        assertTrue(customers.getContent().isEmpty());
        assertEquals(10, customers.getTotalElements());
        assertEquals(2, customers.getTotalPages());
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