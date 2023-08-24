package com.blogify.controller;

import com.blogify.entity.Customer;
import com.blogify.payload.CustomerDto;
import com.blogify.payload.ResponsePage;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Management", description = "Operations related to managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(
            summary = "Get all customers",
            description = "Retrieve a paginated list of customers with optional sorting",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of customers"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token")
            }
    )
    @GetMapping
    public ResponseEntity<ResponsePage<CustomerDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "sort", required = false) String[] sort
    ) {
        Sort sortOrder = PageUtil.parseSort(sort, Customer.class);
        return ResponseEntity.ok(customerService.findAll(page, sortOrder));
    }

    @Operation(
            summary = "Get a customer by ID",
            description = "Retrieve a customer by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved customer"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> findById(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.findById(customerId));
    }

    @Operation(
            summary = "Delete a customer",
            description = "Delete a customer by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted customer"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteById(customerId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Update a customer",
            description = "Update a customer's information by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated customer"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid Token"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Customer not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PutMapping(value = "/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long customerId, @Valid @RequestBody CustomerDto customerDto) {
        return ResponseEntity.ok(customerService.update(customerId, customerDto));
    }

}
