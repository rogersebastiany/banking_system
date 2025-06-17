package com.bank.customerservice.application.service;

import com.bank.customerservice.application.port.in.FindCustomerByIdUseCase;
import com.bank.customerservice.application.port.out.CustomerRepository;
import com.bank.customerservice.domain.exception.CustomerNotFoundException;
import com.bank.customerservice.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FindCustomerService implements FindCustomerByIdUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public CompletableFuture<Customer> findCustomerById(String customerId) {
        return customerRepository.findById(customerId)
                .thenApply(optionalCustomer -> optionalCustomer
                        .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId)));
    }
}
