package com.blogify.controller;

import com.blogify.entity.Category;
import com.blogify.payload.CategoryDto;
import com.blogify.payload.ErrorResponse;
import com.blogify.payload.ResponsePage;
import com.blogify.service.CategoryService;
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
@RequestMapping("/api/v1/categories")
@Tag(name = "Category Management", description = "Operations related to managing categories")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Get all categories",
            description = "Retrieve a paginated list of categories with optional sorting",
            parameters = {
                    @Parameter(name = "page", description = "Page number for pagination", example = "0"),
                    @Parameter(name = "sort", description = "Sorting criteria in the format: [property...],(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.", example = "name,asc")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of categories", content = @Content(schema = @Schema(implementation = ResponsePage.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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
            parameters = {
                    @Parameter(name = "categoryId", description = "ID of the category to be retrieved", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved category", content = @Content(schema = @Schema(implementation = CategoryDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> findById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.findById(categoryId));
    }

    @Operation(
            summary = "Create a new category",
            description = "Create a new category",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Details of the new category to be created", required = true, content = @Content(schema = @Schema(implementation = CategoryDto.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created category", content = @Content(schema = @Schema(implementation = CategoryDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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
            parameters = {
                    @Parameter(name = "categoryId", description = "ID of the category to be updated", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated details of the category", required = true, content = @Content(schema = @Schema(implementation = CategoryDto.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated category", content = @Content(schema = @Schema(implementation = CategoryDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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
            parameters = {
                    @Parameter(name = "categoryId", description = "ID of the category to be deleted", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted category", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long categoryId) {
        categoryService.deleteById(categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
