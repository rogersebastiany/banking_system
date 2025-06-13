package com.bank.customerservice.application.service;

import com.bank.customerservice.application.port.in.RegisterCustomerCommand;
import com.bank.customerservice.application.port.in.RegisterCustomerUseCase;
import com.bank.customerservice.application.port.out.CustomerRepository;
import com.bank.customerservice.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service implementation for the customer registration use case.
 * This class contains the business logic to orchestrate the registration process.
 */
@Service
@Validated // Enables method-level validation for the command object
@RequiredArgsConstructor
@Slf4j // Lombok: Injects a logger instance
public class RegisterCustomerService implements RegisterCustomerUseCase {

    private final CustomerRepository customerRepository;

    /**
     * Registers a new customer based on the provided command.
     * The logic is implemented asynchronously using CompletableFuture.
     *
     * @param command The command containing the new customer's data, marked with @Valid to trigger validation.
     * @return A CompletableFuture that will complete with the created Customer.
     */
    @Override
    public CompletableFuture<Customer> registerCustomer(@Valid RegisterCustomerCommand command) {
        log.info("Attempting to register customer with email: {}", command.getEmail());

        // We start the asynchronous chain here.
        // thenCompose is used when the next action itself returns a CompletableFuture.
        // This avoids nested futures (CompletableFuture<CompletableFuture<Customer>>).
        return customerRepository.findByEmail(command.getEmail())
                .thenCompose(optionalCustomer -> {
                    // Step 1 & 2: Check if email exists and throw exception if it does.
                    if (optionalCustomer.isPresent()) {
                        log.warn("Registration failed: email {} already exists.", command.getEmail());
                        return CompletableFuture.failedFuture(new IllegalStateException("Email already registered"));
                    }

                    // Step 3-6: Email is unique, so create and populate a new Customer object.
                    log.info("Email {} is unique. Creating new customer.", command.getEmail());
                    Customer newCustomer = Customer.builder()
                            .customerId(UUID.randomUUID().toString()) // 5. Generate unique ID
                            .firstName(command.getFirstName())        // 4. Populate from command
                            .lastName(command.getLastName())
                            .email(command.getEmail())
                            .cpf(command.getCpf())
                            .createdAt(Instant.now())                 // 6. Set creation timestamp
                            .build();

                    // Step 7 & 8: Save the new customer and return the result.
                    // The save method also returns a CompletableFuture, which fits perfectly with thenCompose.
                    return customerRepository.save(newCustomer);
                });
    }
}
