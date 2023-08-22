package com.blogify.controller;

import com.blogify.entity.Comment;
import com.blogify.entity.Customer;
import com.blogify.payload.CategoryDto;
import com.blogify.payload.CommentDto;
import com.blogify.payload.ResponsePage;
import com.blogify.service.CommentService;
import com.blogify.service.CustomerService;
import com.blogify.util.PageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ResponsePage<CommentDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "sort", required = false) String[] sort
    ) {
        Sort sortOrder = PageUtil.parseSort(sort, Comment.class);
        return ResponseEntity.ok(commentService.findAll(page, sortOrder));
    }


    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> findById(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.findById(commentId));
    }

    @PostMapping
    public ResponseEntity<CommentDto> create(@Valid @RequestBody CommentDto comment,
                                             Authentication authentication) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(authentication);
        comment.setFullName(authenticatedCustomer.getFullName());

        return new ResponseEntity<>(commentService.create(comment), HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(@PathVariable Long commentId,
                                             @Valid @RequestBody CommentDto comment,
                                             Authentication authentication) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(authentication);
        comment.setFullName(authenticatedCustomer.getFullName());

        return ResponseEntity.ok(commentService.update(commentId, comment));
    }

    private Customer getAuthenticatedCustomer(Authentication authentication) {
        String email = authentication.getName();
        return customerService.findByEmail(email);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.deleteById(commentId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
