package com.blogify.payload;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArticleDto extends EntityDto {

    @Schema(description = "Title of the article",
            example = "Understanding Java Streams",
            nullable = false,
            maxLength = 64)
    @NotNull(message = "Title cannot be null")
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 64, message = "Title cannot be longer than 64 characters")
    private String title;

    @Schema(description = "Description of the article",
            example = "A comprehensive guide to Java Streams",
            nullable = false,
            maxLength = 255)
    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 255, message = "Description cannot be longer than 255 characters")
    private String description;

    @Schema(description = "Content of the article",
            example = "Java Streams provide a modern way to process collections...",
            nullable = false)
    @NotNull(message = "Content cannot be null")
    @NotBlank(message = "Content cannot be blank")
    private String content;

    @Schema(description = "ID of the category to which the article belongs",
            example = "1",
            nullable = true)
    private Long categoryId;

    @ArraySchema(schema = @Schema(description = "Comments on the article",
            nullable = true,
            implementation = CommentDto.class))
    private List<CommentDto> comments;
}
