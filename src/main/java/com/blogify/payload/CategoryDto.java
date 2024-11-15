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
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDto extends EntityDto {

    @Schema(description = "Name of the category",
            example = "Technology",
            nullable = false,
            maxLength = 255)
    @NotNull(message = "Category name cannot be null")
    @NotBlank(message = "Category name cannot be blank")
    @Size(max = 255, message = "Category name cannot exceed 255 characters")
    private String name;

    @ArraySchema(schema = @Schema(description = "Articles for the category",
            nullable = true,
            implementation = ArticleDto.class))
    private List<ArticleDto> articles;

}