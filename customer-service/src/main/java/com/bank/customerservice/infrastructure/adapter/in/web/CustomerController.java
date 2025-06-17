package com.bank.customerservice.infraestructure.adapter.in.web;

import com.bank.customerservice.application.port.in.RegisterCustomerCommand;
import com.bank.customerservice.application.port.in.RegisterCustomerUseCase;
import com.bank.customerservice.infraestructure.adapter.in.web.dto.CustomerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * The REST Controller (inbound adapter) that exposes our customer services over HTTP.
 * It depends on the use case interface (port), not the concrete service implementation.
 */
@RestController
@RequestMapping("/api/v1/customers") // Base path for all endpoints in this controller
@RequiredArgsConstructor
public class CustomerController {

    private final RegisterCustomerUseCase registerCustomerUseCase;

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
}