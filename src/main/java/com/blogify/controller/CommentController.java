package com.blogify.controller;

import com.blogify.entity.Comment;
import com.blogify.entity.Customer;
import com.blogify.payload.CommentDto;
import com.blogify.payload.ResponsePage;
import com.blogify.service.CommentService;
import com.blogify.service.CustomerService;
import com.blogify.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "Comment Management", description = "Operations related to managing comments")
public class CommentController {

    private final CommentService commentService;
    private final CustomerService customerService;

    @Operation(
            summary = "Get all comments",
            description = "Retrieve a paginated list of comments with optional sorting",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of comments"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token")
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
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved comment"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "404", description = "Comment not found")
            }
    )
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> findById(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.findById(commentId));
    }

    @Operation(
            summary = "Create a new comment",
            description = "Create a new comment",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created comment"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token")
            }
    )
    @PostMapping
    public ResponseEntity<CommentDto> create(@Valid @RequestBody CommentDto comment,
                                             Authentication authentication) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(authentication);
        comment.setFullName(authenticatedCustomer.getFullName());

        return new ResponseEntity<>(commentService.create(comment), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update a comment",
            description = "Update a comment's information by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated comment"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "404", description = "Comment not found")
            }
    )
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(@PathVariable Long commentId,
                                             @Valid @RequestBody CommentDto comment,
                                             Authentication authentication) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(authentication);
        comment.setFullName(authenticatedCustomer.getFullName());

        return ResponseEntity.ok(commentService.update(commentId, comment));
    }

    @Operation(
            summary = "Delete a comment",
            description = "Delete a comment by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted comment"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "404", description = "Comment not found")
            }
    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.deleteById(commentId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Customer getAuthenticatedCustomer(Authentication authentication) {
        String email = authentication.getName();
        return customerService.findByEmail(email);
    }
}
