package com.blogify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article extends BaseEntity {

    @Column(name = "title", nullable = false, length = 64, unique = true)
    private String title;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "content", nullable = false, columnDefinition = "CLOB")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Category category;

    @OneToMany(mappedBy = "article" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<Comment> comments;

    public void addComment(Comment comment) {
        if(this.comments == null) {
            this.comments = new ArrayList<>();
        }

        comment.setArticle(this);
        comments.add(comment);
    }

}
