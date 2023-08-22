package com.blogify.controller;

import com.blogify.entity.Customer;
import com.blogify.payload.ArticleDto;
import com.blogify.payload.CustomerDto;
import com.blogify.payload.ResponsePage;
import com.blogify.service.CustomerService;
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
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ResponsePage<CustomerDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "sort", required = false) String[] sort
    ) {
        Sort sortOrder = PageUtil.parseSort(sort, Customer.class);
        return ResponseEntity.ok(customerService.findAll(page, sortOrder));
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> findById(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.findById(customerId));
    }

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteById(customerId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long customerId, @Valid @RequestBody CustomerDto customerDto) {
        return ResponseEntity.ok(customerService.update(customerId, customerDto));
    }

}
