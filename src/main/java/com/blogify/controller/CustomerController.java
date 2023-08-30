package com.blogify.controller;

import com.blogify.entity.Customer;
import com.blogify.payload.CustomerDto;
import com.blogify.payload.ErrorResponse;
import com.blogify.payload.ResponsePage;
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
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customers")
@Tag(name = "Customer Management", description = "Operations related to managing customers")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(
            summary = "Get all customers",
            description = "Retrieve a paginated list of customers with optional sorting",
            parameters = {
                    @Parameter(name = "page", description = "Page number for pagination",
                            example = "0"),
                    @Parameter(name = "sort", description = "Sorting criteria in the format: " +
                            "[property...],(asc|desc). Default sort order is ascending. Multiple " +
                            "sort criteria are supported.", example = "lastName,asc")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list" +
                            " of customers", content = @Content(schema = @Schema(implementation =
                            ResponsePage.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid " +
                            "Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content
                            = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found",
                            content = @Content(schema = @Schema(implementation =
                                    ErrorResponse.class)))
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
            parameters = {
                    @Parameter(name = "customerId", description = "ID of the customer to be " +
                            "retrieved", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved " +
                            "customer", content = @Content(schema = @Schema(implementation =
                            CustomerDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid " +
                            "Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content
                            = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found",
                            content = @Content(schema = @Schema(implementation =
                                    ErrorResponse.class)))
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
            parameters = {
                    @Parameter(name = "customerId", description = "ID of the customer to be " +
                            "deleted", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted " +
                            "customer", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid " +
                            "Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content
                            = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found",
                            content = @Content(schema = @Schema(implementation =
                                    ErrorResponse.class)))
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
            parameters = {
                    @Parameter(name = "customerId", description = "ID of the customer to be " +
                            "updated", required = true)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description =
                    "Updated details of the customer", required = true, content =
            @Content(schema = @Schema(implementation = CustomerDto.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated " +
                            "customer", content = @Content(schema = @Schema(implementation =
                            CustomerDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid " +
                            "Token", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content
                            = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found",
                            content = @Content(schema = @Schema(implementation =
                                    ErrorResponse.class)))
            }
    )
    @PutMapping(value = "/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long customerId,
                                                      @Valid @RequestBody CustomerDto customerDto) {
        return ResponseEntity.ok(customerService.update(customerId, customerDto));
    }

}
