package com.bank.customerservice.application.port.in;

import com.bank.customerservice.domain.model.Customer;

import java.util.concurrent.CompletableFuture;

/**
 * Input port for the use case of finding a single customer by their ID.
 */
public interface FindCustomerByIdUseCase {
    CompletableFuture<Customer> findCustomerById(String customerId);
}
