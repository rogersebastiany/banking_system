package com.bank.customerservice.application.port.in;

import com.bank.customerservice.domain.model.Customer;

import jakarta.validation.Valid;

import java.util.concurrent.CompletableFuture;

/**
 * Input port for the customer registration use case.
 * Defines the contract for the primary actor's interaction (registering a customer).
 */
public interface RegisterCustomerUseCase {

    /**
     * Executes the customer registration logic.
     * @param command The command containing all necessary data for registration.
     * @return A CompletableFuture that completes with the newly created Customer.
     */
    CompletableFuture<Customer> registerCustomer(@Valid RegisterCustomerCommand command);
}
