package com.blogify.util;

import com.blogify.entity.Article;
import com.blogify.payload.ArticleDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ArticleTestUtil {

    public static Article generateDummyArticle() {
        Article article = new Article();
        article.setTitle("Article with Comments");
        article.setDescription("This article has comments");
        article.setContent("Content of the article with comments");
        return article;
    }

    public static Article generateDummyComment() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Effective java");
        article.setContent("Content");
        article.setDescription("Description");

        return article;
    }

    public static ArticleDto generateDummyCommentDto() {
        return TestUtil.map(ArticleDto.class, generateDummyComment());
    }

}
