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
    public ResponseEntity<List<ArticleDto>> findAll() {
        return ResponseEntity.ok(articleService.findAll());
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> findById(@PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.findById(articleId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    public ResponseEntity<ArticleDto> create(@Valid @RequestBody ArticleDto articleDto) {
        return new ResponseEntity<>(articleService.create(articleDto), HttpStatus.CREATED);
    }


    @PutMapping("/{articleId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    public ResponseEntity<ArticleDto> update(@PathVariable Long articleId,
                                                    @Valid @RequestBody ArticleDto articleDto) {
        return ResponseEntity.ok(articleService.update(articleId, articleDto));
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long customerId) {
        articleService.deleteById(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
