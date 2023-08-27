package com.blogify.service;

import com.blogify.entity.Comment;
import com.blogify.exception.ApiException;
import com.blogify.payload.CommentDto;
import com.blogify.payload.ResponsePage;
import com.blogify.repository.ArticleRepository;
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
    private final ArticleRepository articleRepository;

    @Override
    public CommentDto create(CommentDto newComment) {
        validateArticleExist(newComment.getArticleId());
        validateComment(null, newComment.getArticleId(),  newComment.getFullName());
        save(newComment);

        return newComment;
    }

    @Override
    public CommentDto update(Long id, CommentDto newComment) {
        newComment.setId(id);
        validateArticleExist(newComment.getArticleId());
        validateComment(id,newComment.getArticleId(), newComment.getFullName());
        save(newComment);

        return newComment;
    }

    private void save(CommentDto newComment) {
        Comment save = commentRepository.save(toEntity(newComment));
        newComment.setId(save.getId());
    }

    private void validateArticleExist(Long articleId) {
        boolean articleExist = articleRepository.existsById(articleId);
        if (!articleExist) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Article not found");
        }
    }

    private void validateComment(Long commentId, Long articleId, String fullName) {
        commentRepository.findByFullNameAndArticleId(fullName, articleId).ifPresent(comment -> {
            if (comment.getArticle().getId().equals(articleId) && !comment.getId().equals(commentId)) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                    "You already posted comment for this Article"
                );
            }
        });

        if (commentId != null && !commentRepository.existsById(commentId)) {
            throw generateNotFound();
        }
    }

    @Override
    public ResponsePage<CommentDto> findAll(Integer pageNum, Sort sort) {
        Page<Comment> page = commentRepository.findAll(PageRequest.of(pageNum, PAGE_SIZE, sort));

        return ResponsePage.<CommentDto>builder()
                .page(pageNum)
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
