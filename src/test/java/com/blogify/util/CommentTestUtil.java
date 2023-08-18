package com.blogify.util;

import com.blogify.entity.Article;
import com.blogify.entity.Comment;

public class CommentTestUtil {

    public static Comment generateDummyComment(String fullName, String content) {
        Comment comment = new Comment();
        comment.setFullName(fullName);
        comment.setContent(content);
        return comment;
    }
}