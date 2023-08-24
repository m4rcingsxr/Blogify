package com.blogify.controller;

import com.blogify.entity.Category;
import com.blogify.payload.CategoryDto;
import com.blogify.payload.ResponsePage;
import com.blogify.service.CategoryService;
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
@RequestMapping("/api/v1/categories")
@Tag(name = "Category Management", description = "Operations related to managing categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Get all categories",
            description = "Retrieve a paginated list of categories with optional sorting",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of categories"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token")
            }
    )
    @GetMapping
    public ResponseEntity<ResponsePage<CategoryDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "sort", required = false) String[] sort
    ) {
        Sort sortOrder = PageUtil.parseSort(sort, Category.class);
        return ResponseEntity.ok(categoryService.findAll(page, sortOrder));
    }

    @Operation(
            summary = "Get a category by ID",
            description = "Retrieve a category by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved category"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> findById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.findById(categoryId));
    }

    @Operation(
            summary = "Create a new category",
            description = "Create a new category",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created category"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token")
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.create(categoryDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update a category",
            description = "Update a category's information by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated category"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> update(@PathVariable Long categoryId, @Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.update(categoryId, categoryDto));
    }

    @Operation(
            summary = "Delete a category",
            description = "Delete a category by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted category"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long categoryId) {
        categoryService.deleteById(categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
