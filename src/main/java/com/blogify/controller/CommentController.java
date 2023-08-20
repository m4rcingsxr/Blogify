package com.blogify.controller;

import com.blogify.entity.Customer;
import com.blogify.payload.CommentDto;
import com.blogify.service.CommentService;
import com.blogify.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> findAll() {
        return ResponseEntity.ok(commentService.findAll());
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
