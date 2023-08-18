package com.blogify.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.w3c.dom.Entity;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDto extends EntityDto {

    @NotNull
    @Size(max = 500, message = "Content cannot exceed 500 characters.")
    private String content;

    @NotNull(message = "Article id is required")
    @NotBlank(message = "Article id cannot be blank")
    private Long articleId;

}