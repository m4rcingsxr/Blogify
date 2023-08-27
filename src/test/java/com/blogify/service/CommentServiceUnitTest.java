package com.blogify.service;

import com.blogify.entity.Article;
import com.blogify.entity.Comment;
import com.blogify.exception.ApiException;
import com.blogify.payload.CommentDto;
import com.blogify.payload.ResponsePage;
import com.blogify.repository.ArticleRepository;
import com.blogify.repository.CommentRepository;
import com.blogify.util.ArticleTestUtil;
import com.blogify.util.CommentTestUtil;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceUnitTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    void givenNewComment_whenCreate_thenRepositorySaveIsInvoked() {
        CommentDto newCommentDto = CommentTestUtil.generateDummyCommentDto();
        Comment newComment = CommentTestUtil.toEntity(newCommentDto);

        when(commentRepository.save(any(Comment.class))).thenReturn(newComment);
        when(modelMapper.map(newCommentDto, Comment.class)).thenReturn(newComment);
        when(articleRepository.existsById(newCommentDto.getArticleId())).thenReturn(true);

        CommentDto createdComment = commentService.create(newCommentDto);

        assertEquals(newCommentDto, createdComment);

        verify(commentRepository, times(1)).save(newComment);
        verify(articleRepository, times(1)).existsById(newCommentDto.getArticleId());
    }

    @Test
    void givenExistingComment_whenUpdate_thenRepositorySaveIsInvoked() {
        Comment existingComment = CommentTestUtil.generateDummyComment();
        existingComment.setId(1L);

        CommentDto updatedCommentDto = CommentTestUtil.generateDummyCommentDto();
        updatedCommentDto.setId(1L);

        when(commentRepository.existsById(existingComment.getId())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(existingComment);
        when(modelMapper.map(updatedCommentDto, Comment.class)).thenReturn(existingComment);
        when(articleRepository.existsById(updatedCommentDto.getArticleId())).thenReturn(true);

        CommentDto updatedComment = commentService.update(existingComment.getId(), updatedCommentDto);

        assertEquals(updatedCommentDto, updatedComment);

        verify(commentRepository, times(1)).existsById(existingComment.getId());
        verify(commentRepository, times(1)).save(existingComment);
        verify(articleRepository, times(1)).existsById(updatedCommentDto.getArticleId());
    }

    @Test
    void givenExistingComment_whenFindById_thenFindCommentIsInvoked() {
        Comment existingComment = CommentTestUtil.generateDummyComment();
        existingComment.setId(1L);
        CommentDto commentDto = CommentTestUtil.toDto(existingComment);

        when(commentRepository.findById(existingComment.getId())).thenReturn(Optional.of(existingComment));
        when(modelMapper.map(existingComment, CommentDto.class)).thenReturn(commentDto);

        CommentDto foundComment = commentService.findById(existingComment.getId());

        assertEquals(commentDto, foundComment);

        verify(commentRepository, times(1)).findById(existingComment.getId());
    }

    @Test
    void givenNotExistingComment_whenFindById_thenApiExceptionIsThrown() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> commentService.findById(-1L));

        verify(commentRepository, times(1)).findById(-1L);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void givenExistingComment_whenDeleteById_thenRepositoryDeleteIsInvoked() {
        Comment existingComment = CommentTestUtil.generateDummyComment();
        existingComment.setId(1L);

        when(commentRepository.findById(existingComment.getId())).thenReturn(Optional.of(existingComment));

        commentService.deleteById(existingComment.getId());

        verify(commentRepository, times(1)).findById(existingComment.getId());
        verify(commentRepository, times(1)).delete(existingComment);
    }

    @Test
    void givenNotExistingComment_whenDeleteById_thenApiExceptionIsThrown() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> commentService.deleteById(-1L));

        verify(commentRepository, times(1)).findById(-1L);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void givenCommentsExist_whenFindAll_thenRepositoryFindAllIsInvoked() {
        // Arrange
        Comment existingComment = CommentTestUtil.generateDummyComment();
        existingComment.setId(1L);
        CommentDto commentDto = CommentTestUtil.toDto(existingComment);
        Page<Comment> commentPage = new PageImpl<>(List.of(existingComment), PageRequest.of(0, CommentService.PAGE_SIZE), 1);

        when(commentRepository.findAll(any(PageRequest.class))).thenReturn(commentPage);
        when(modelMapper.map(existingComment, CommentDto.class)).thenReturn(commentDto);

        // Act
        ResponsePage<CommentDto> responsePage = commentService.findAll(0, Sort.unsorted());

        // Assert
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        assertEquals(commentDto, responsePage.getContent().get(0));
        verify(commentRepository, times(1)).findAll(any(PageRequest.class));
        verify(modelMapper, times(1)).map(existingComment, CommentDto.class);
    }

}
