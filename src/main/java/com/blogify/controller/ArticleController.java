package com.blogify.controller;

import com.blogify.entity.Article;
import com.blogify.payload.ArticleDto;
import com.blogify.payload.ErrorResponse;
import com.blogify.payload.ResponsePage;
import com.blogify.service.ArticleService;
import com.blogify.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/articles")
@Tag(name = "Article Management", description = "Operations related to managing articles")
@SecurityRequirement(name = "bearerAuth")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(
            summary = "Get all articles",
            description = "Retrieve a paginated list of articles with optional sorting",
            parameters = {
                    @Parameter(name = "page", description = "Page number for pagination", example = "0"),
                    @Parameter(name = "sort", description = "Sorting criteria in the format: [property...],(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.", example = "title,asc")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of articles", content = @Content(schema = @Schema(implementation = ResponsePage.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Article not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<ResponsePage<ArticleDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "sort", required = false) String[] sort
    ) {
        Sort sortOrder = PageUtil.parseSort(sort, Article.class);
        return ResponseEntity.ok(articleService.findAll(page, sortOrder));
    }

    @Operation(
            summary = "Get an article by ID",
            description = "Retrieve an article by its ID",
            parameters = {
                    @Parameter(name = "articleId", description = "ID of the article to be retrieved", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved article", content = @Content(schema = @Schema(implementation = ArticleDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Article not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> findById(@PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.findById(articleId));
    }

    @Operation(
            summary = "Create a new article",
            description = "Create a new article",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Details of the new article to be created", required = true, content = @Content(schema = @Schema(implementation = ArticleDto.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created article", content = @Content(schema = @Schema(implementation = ArticleDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ArticleDto> create(@Valid @RequestBody ArticleDto articleDto) {
        return new ResponseEntity<>(articleService.create(articleDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update an article",
            description = "Update an article's information by its ID",
            parameters = {
                    @Parameter(name = "articleId", description = "ID of the article to be updated", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated details of the article", required = true, content = @Content(schema = @Schema(implementation = ArticleDto.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated article", content = @Content(schema = @Schema(implementation = ArticleDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Article not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PutMapping("/{articleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ArticleDto> update(@PathVariable Long articleId,
                                             @Valid @RequestBody ArticleDto articleDto) {
        return ResponseEntity.ok(articleService.update(articleId, articleDto));
    }

    @Operation(
            summary = "Delete an article",
            description = "Delete an article by its ID",
            parameters = {
                    @Parameter(name = "articleId", description = "ID of the article to be deleted", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated article", content = @Content(schema = @Schema(implementation = ArticleDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Article not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/{articleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Void> deleteById(@PathVariable Long articleId) {
        articleService.deleteById(articleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
