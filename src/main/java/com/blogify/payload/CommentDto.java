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

    @NotNull(message = "Content cannot be null")
    @NotNull(message = "Content cannot be blank")
    @Size(max = 500, message = "Content cannot exceed 500 characters.")
    private String content;

    @NotNull(message = "Article id is required")
    private Long articleId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String fullName;

}