package com.blogify.controller;

import com.blogify.entity.Article;
import com.blogify.payload.ArticleDto;
import com.blogify.payload.ResponsePage;
import com.blogify.service.ArticleService;
import com.blogify.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class ArticleController {

    private final ArticleService articleService;

    @Operation(
            summary = "Get all articles",
            description = "Retrieve a paginated list of articles with optional sorting",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of articles"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token")
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
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved article"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "404", description = "Article not found")
            }
    )
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> findById(@PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.findById(articleId));
    }

    @Operation(
            summary = "Create a new article",
            description = "Create a new article",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created article"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token")
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    public ResponseEntity<ArticleDto> create(@Valid @RequestBody ArticleDto articleDto) {
        return new ResponseEntity<>(articleService.create(articleDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update an article",
            description = "Update an article's information by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated article"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "404", description = "Article not found")
            }
    )
    @PutMapping("/{articleId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    public ResponseEntity<ArticleDto> update(@PathVariable Long articleId,
                                             @Valid @RequestBody ArticleDto articleDto) {
        return ResponseEntity.ok(articleService.update(articleId, articleDto));
    }

    @Operation(
            summary = "Delete an article",
            description = "Delete an article by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted article"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "404", description = "Article not found")
            }
    )
    @DeleteMapping("/{articleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long articleId) {
        articleService.deleteById(articleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
