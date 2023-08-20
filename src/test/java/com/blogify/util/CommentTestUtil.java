package com.blogify.util;

import com.blogify.entity.Comment;
import com.blogify.payload.CommentDto;

public class CommentTestUtil {

    public static Comment generateDummyComment(String fullName, String content) {
        Comment comment = new Comment();
        comment.setFullName(fullName);
        comment.setContent(content);
        return comment;
    }

    public static CommentDto generateDummyCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setFullName("John Doe");
        commentDto.setContent("This is a dummy comment.");
        return commentDto;
    }

    public static Comment toEntity(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setFullName(commentDto.getFullName());
        comment.setContent(commentDto.getContent());
        return comment;
    }

    public static CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setFullName(comment.getFullName());
        commentDto.setContent(comment.getContent());
        return commentDto;
    }
}