package com.blogify.service;

import com.blogify.entity.Comment;
import com.blogify.exception.ApiException;
import com.blogify.payload.CommentDto;
import com.blogify.repository.CommentRepository;
import com.blogify.util.CommentTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceUnitTest {

    @Mock
    private CommentRepository commentRepository;

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

        CommentDto createdComment = commentService.create(newCommentDto);

        assertEquals(newCommentDto, createdComment);

        verify(commentRepository, times(1)).save(newComment);
    }

    @Test
    void givenExistingComment_whenUpdate_thenRepositorySaveIsInvoked() {
        Comment existingComment = CommentTestUtil.generateDummyComment("John Doe", "Java in practice");
        existingComment.setId(1L);

        CommentDto updatedCommentDto = CommentTestUtil.generateDummyCommentDto();
        updatedCommentDto.setId(1L);

        when(commentRepository.existsById(existingComment.getId())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(existingComment);
        when(modelMapper.map(updatedCommentDto, Comment.class)).thenReturn(existingComment);

        CommentDto updatedComment = commentService.update(existingComment.getId(), updatedCommentDto);

        assertEquals(updatedCommentDto, updatedComment);

        verify(commentRepository, times(1)).existsById(existingComment.getId());
        verify(commentRepository, times(1)).save(existingComment);
    }

    @Test
    void givenExistingComment_whenFindById_thenFindCommentIsInvoked() {
        Comment existingComment = CommentTestUtil.generateDummyComment("John Doe", "Java in practice");
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
        Comment existingComment = CommentTestUtil.generateDummyComment("John Doe", "Java in practice");
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
        Comment existingComment = CommentTestUtil.generateDummyComment("John Doe", "Java in practice");
        CommentDto commentDto = CommentTestUtil.toDto(existingComment);

        when(commentRepository.findAll()).thenReturn(List.of(existingComment));
        when(modelMapper.map(existingComment, CommentDto.class)).thenReturn(commentDto);

        List<CommentDto> comments = commentService.findAll();

        assertEquals(1, comments.size());
        assertEquals(commentDto, comments.get(0));

        verify(commentRepository, times(1)).findAll();
    }
}
