package com.blogify.controller;

import com.blogify.Constants;
import com.blogify.entity.Comment;
import com.blogify.entity.Customer;
import com.blogify.payload.CommentDto;
import com.blogify.payload.ErrorResponse;
import com.blogify.payload.ResponsePage;
import com.blogify.service.CommentService;
import com.blogify.service.CustomerService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/"+ Constants.VERSION +"/comments")
@Tag(name = "Comment Management", description = "Operations related to managing comments")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;
    private final CustomerService customerService;

    @Operation(
            summary = "Get all comments",
            description = "Retrieve a paginated list of comments with optional sorting",
            parameters = {
                    @Parameter(name = "page", description = "Page number for pagination", example = "0"),
                    @Parameter(name = "sort", description = "Sorting criteria in the format: [property...],(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.", example = "createdAt,desc")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of comments", content = @Content(schema = @Schema(implementation = ResponsePage.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<ResponsePage<CommentDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "sort", required = false) String[] sort
    ) {
        Sort sortOrder = PageUtil.parseSort(sort, Comment.class);
        return ResponseEntity.ok(commentService.findAll(page, sortOrder));
    }

    @Operation(
            summary = "Get a comment by ID",
            description = "Retrieve a comment by its ID",
            parameters = {
                    @Parameter(name = "commentId", description = "ID of the comment to be retrieved", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved comment", content = @Content(schema = @Schema(implementation = CommentDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> findById(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.findById(commentId));
    }

    @Operation(
            summary = "Create a new comment",
            description = "Create a new comment",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Details of the new comment to be created", required = true, content = @Content(schema = @Schema(implementation = CommentDto.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created comment", content = @Content(schema = @Schema(implementation = CommentDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<CommentDto> create(@Valid @RequestBody CommentDto comment, Authentication authentication) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(authentication);
        comment.setFullName(authenticatedCustomer.getFullName());

        return new ResponseEntity<>(commentService.create(comment), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update a comment",
            description = "Update a comment's information by its ID",
            parameters = {
                    @Parameter(name = "commentId", description = "ID of the comment to be updated", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated details of the comment", required = true, content = @Content(schema = @Schema(implementation = CommentDto.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated comment", content = @Content(schema = @Schema(implementation = CommentDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<CommentDto> update(@PathVariable Long commentId, @Valid @RequestBody CommentDto comment, Authentication authentication) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(authentication);
        comment.setFullName(authenticatedCustomer.getFullName());

        return ResponseEntity.ok(commentService.update(commentId, comment));
    }

    @Operation(
            summary = "Delete a comment",
            description = "Delete a comment by its ID",
            parameters = {
                    @Parameter(name = "commentId", description = "ID of the comment to be deleted", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted comment", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.deleteById(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Customer getAuthenticatedCustomer(Authentication authentication) {
        String email = authentication.getName();
        return customerService.findByEmail(email);
    }
}
