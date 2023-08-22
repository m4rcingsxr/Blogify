package com.blogify.controller;

import com.blogify.entity.Category;
import com.blogify.payload.CategoryDto;
import com.blogify.payload.CategoryDto;
import com.blogify.payload.ResponsePage;
import com.blogify.service.CategoryService;
import com.blogify.util.PageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ResponsePage<CategoryDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "sort", required = false) String[] sort
    ) {
        Sort sortOrder = PageUtil.parseSort(sort, Category.class);
        return ResponseEntity.ok(categoryService.findAll(page, sortOrder));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.create(categoryDto), HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> update(@PathVariable Long categoryId, @Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.update(categoryId, categoryDto));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> findById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.findById(categoryId));
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long categoryId) {
        categoryService.deleteById(categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
