package com.blogify.service;

import com.blogify.entity.Comment;
import com.blogify.exception.ApiException;
import com.blogify.payload.CommentDto;
import com.blogify.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService implements EntityService<CommentDto> {

    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    @Override
    public CommentDto create(CommentDto newComment) {
        validateComment(null, newComment.getFullName());
        save(newComment);

        return newComment;
    }

    @Override
    public CommentDto update(Long id, CommentDto newComment) {
        validateComment(id, newComment.getFullName());
        save(newComment);

        return newComment;
    }

    private void save(CommentDto newComment) {
        Comment save = commentRepository.save(toEntity(newComment));
        newComment.setId(save.getId());
    }

    private void validateComment(Long id, String fullName) {
        commentRepository.findByFullName(fullName).ifPresent(comment -> {
            if (!comment.getId().equals(id)) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                    "User '" + fullName + "' already posted comment for this " + "Article."
                );
            }
        });

        if (id != null && !commentRepository.existsById(id)) {
            throw generateNotFound();
        }
    }

    @Override
    public List<CommentDto> findAll() {
        return commentRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public CommentDto findById(Long id) {
        return toDto(findByIdInternal(id));
    }

    @Override
    public void deleteById(Long id) {
        Comment comment = findByIdInternal(id);
        commentRepository.delete(comment);
    }

    private Comment findByIdInternal(Long id) {
        return commentRepository.findById(id).orElseThrow(this::generateNotFound);
    }

    private ApiException generateNotFound() {
        return new ApiException(HttpStatus.NOT_FOUND, "Comment not found.");
    }

    private CommentDto toDto(Comment comment) {
        return modelMapper.map(comment, CommentDto.class);
    }

    private Comment toEntity(CommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }

}
