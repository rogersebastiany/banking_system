package com.bank.customerservice.application.port.out;

import com.bank.customerservice.domain.model.Customer;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Outgoing port for customer persistence.
 * This interface defines the contract that the core application (use cases)
 * uses to interact with the persistence layer, without knowing the underlying
 * technology (e.g., DynamoDB, JPA, etc.).
 *
 * Methods return CompletableFuture to support the async nature of the DynamoDB client.
 */
public interface CustomerRepository {

    /**
     * Saves a new customer or updates an existing one.
     *
     * @param customer The customer object to be saved.
     * @return A CompletableFuture that completes with the saved customer.
     */
    CompletableFuture<Customer> save(Customer customer);

    /**
     * Finds a customer by their unique customer ID.
     *
     * @param customerId The unique ID of the customer.
     * @return A CompletableFuture that completes with an Optional containing the customer if found, or an empty Optional if not.
     */
    CompletableFuture<Optional<Customer>> findById(String customerId);

    /**
     * Finds a customer by their email address.
     *
     * @param email The email address to search for.
     * @return A CompletableFuture that completes with an Optional containing the customer if found, or an empty Optional if not.
     */
    CompletableFuture<Optional<Customer>> findByEmail(String email);
}
