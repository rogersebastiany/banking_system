package com.bank.customerservice.application.service;

import com.bank.customerservice.application.port.out.CustomerRepository;
import com.bank.customerservice.domain.exception.CustomerNotFoundException;
import com.bank.customerservice.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private FindCustomerService findCustomerService;

    @Test
    void shouldReturnCustomer_whenIdExists() throws ExecutionException, InterruptedException {
        // ARRANGE
        String customerId = "test-id-123";
        Customer expectedCustomer = Customer.builder().customerId(customerId).firstName("John").build();

        when(customerRepository.findById(customerId))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(expectedCustomer)));

        // ACT
        CompletableFuture<Customer> future = findCustomerService.findCustomerById(customerId);
        Customer actualCustomer = future.get();

        // ASSERT
        assertThat(actualCustomer).isNotNull();
        assertThat(actualCustomer.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void shouldThrowCustomerNotFoundException_whenIdDoesNotExist() {
        // ARRANGE
        String customerId = "non-existent-id";

        when(customerRepository.findById(customerId))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // ACT
        CompletableFuture<Customer> future = findCustomerService.findCustomerById(customerId);

        // ASSERT
        assertThatThrownBy(future::get)
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(CustomerNotFoundException.class);
    }
}
