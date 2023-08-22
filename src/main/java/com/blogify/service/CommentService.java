package com.blogify.service;

import com.blogify.entity.Comment;
import com.blogify.exception.ApiException;
import com.blogify.payload.CommentDto;
import com.blogify.payload.ResponsePage;
import com.blogify.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService implements EntityService<CommentDto> {

    public static final int PAGE_SIZE = 10;

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
    public ResponsePage<CommentDto> findAll(Integer pageNum, Sort sort) {
        Page<Comment> page = commentRepository.findAll(PageRequest.of(pageNum, PAGE_SIZE, sort));

        return ResponsePage.<CommentDto>builder()
                .pageSize(PAGE_SIZE)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(page.getContent().stream().map(this::mapToDto).toList())
                .build();
    }

    @Override
    public CommentDto findById(Long id) {
        return mapToDto(findByIdInternal(id));
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

    private CommentDto mapToDto(Comment comment) {
        return modelMapper.map(comment, CommentDto.class);
    }

    private Comment toEntity(CommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }

}
