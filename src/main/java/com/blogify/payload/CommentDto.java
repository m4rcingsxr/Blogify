package com.blogify.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDto extends EntityDto {

    @Schema(description = "Content of the comment",
            example = "This is a very insightful article!",
            nullable = false,
            maxLength = 500)
    @NotNull(message = "Content cannot be null")
    @NotNull(message = "Content cannot be blank")
    @Size(max = 500, message = "Content cannot exceed 500 characters.")
    private String content;

    @Schema(description = "ID of the article to which the comment belongs",
            example = "1",
            nullable = false)
    @NotNull(message = "Article id is required")
    private Long articleId;

    @Schema(description = "Full name of the commenter",
            example = "John Doe",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String fullName;

}
