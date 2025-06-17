package com.bank.customerservice.infrastructure.adapter.in.web.dto;

import com.bank.customerservice.domain.model.Customer;

import java.time.Instant;

/**
 * A DTO representing the data sent back to the client after a customer is created.
 * Using a record simplifies the creation of this immutable data carrier.
 */
public record CustomerResponse(
        String customerId,
        String firstName,
        String lastName,
        String email,
        Instant createdAt
) {
    /**
     * A factory method to easily convert from our internal domain model
     * to this public-facing DTO.
     * @param customer The internal Customer domain object.
     * @return A new CustomerResponse object.
     */
    public static CustomerResponse fromDomain(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCreatedAt()
        );
    }
}
