package com.blogify.util;

import com.blogify.entity.Comment;
import com.blogify.payload.CommentDto;

public class CommentTestUtil {

    public static Comment generateDummyComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setFullName("Luke Skywalker");
        comment.setContent("Content");

        return comment;
    }

    public static CommentDto generateDummyCommentDto() {
        return toDto(generateDummyComment());
    }

    public static Comment toEntity(CommentDto commentDto) {
        return TestUtil.map(Comment.class, commentDto);
    }

    public static CommentDto toDto(Comment comment) {
        return TestUtil.map(CommentDto.class, comment);
    }
}