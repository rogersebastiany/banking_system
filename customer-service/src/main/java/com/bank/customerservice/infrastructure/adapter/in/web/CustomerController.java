package com.bank.customerservice.infrastructure.adapter.in.web;

import com.bank.customerservice.application.port.in.FindCustomerByIdUseCase;
import com.bank.customerservice.application.port.in.RegisterCustomerCommand;
import com.bank.customerservice.application.port.in.RegisterCustomerUseCase;
import com.bank.customerservice.infrastructure.adapter.in.web.dto.CustomerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * The REST Controller (inbound adapter) that exposes our customer services over HTTP.
 * It depends on the use case interfaces (ports), not the concrete service implementations.
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final RegisterCustomerUseCase registerCustomerUseCase;
    private final FindCustomerByIdUseCase findCustomerByIdUseCase; // The new use case for finding customers

    /**
     * Handles the HTTP POST request to register a new customer.
     * @param command The request body containing customer details.
     * @return A CompletableFuture that resolves to a 201 CREATED response with the new customer's data.
     */
    @PostMapping
    public CompletableFuture<ResponseEntity<CustomerResponse>> registerCustomer(
            @Valid @RequestBody RegisterCustomerCommand command) {

        return registerCustomerUseCase.registerCustomer(command)
                .thenApply(createdCustomer ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(CustomerResponse.fromDomain(createdCustomer))
                );
    }

    /**
     * Handles the HTTP GET request to find a customer by their unique ID.
     * @param customerId The ID of the customer, passed in the URL path.
     * @return A CompletableFuture that resolves to a 200 OK response with the customer's data,
     * or a 404 NOT FOUND if the CustomerNotFoundException is thrown from the service.
     */
    @GetMapping("/{customerId}")
    public CompletableFuture<ResponseEntity<CustomerResponse>> findCustomerById(
            @PathVariable String customerId) {

        return findCustomerByIdUseCase.findCustomerById(customerId)
                .thenApply(customer -> ResponseEntity.ok(CustomerResponse.fromDomain(customer)));
    }
}
