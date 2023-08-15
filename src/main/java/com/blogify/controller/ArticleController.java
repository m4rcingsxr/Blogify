package com.blogify.controller;

import com.blogify.payload.ArticleDto;
import com.blogify.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleDto>> getAllArticles() {
        return ResponseEntity.ok(articleService.findAll());
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> getArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.findById(articleId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    public ResponseEntity<ArticleDto> createArticle(@Valid @RequestBody ArticleDto articleDto) {
        return ResponseEntity.ok(articleService.create(articleDto));
    }


    @PutMapping("/{articleId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    public ResponseEntity<ArticleDto> updateArticle(@PathVariable Long articleId,
                                                    @Valid @RequestBody ArticleDto articleDto) {
        return ResponseEntity.ok(articleService.update(articleId, articleDto));
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long customerId) {
        articleService.deleteById(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
