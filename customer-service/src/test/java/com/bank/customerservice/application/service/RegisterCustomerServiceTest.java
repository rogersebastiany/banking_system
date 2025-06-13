package com.bank.customerservice.application.service;

import com.bank.customerservice.application.port.in.RegisterCustomerCommand;
import com.bank.customerservice.application.port.out.CustomerRepository;
import com.bank.customerservice.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RegisterCustomerService.
 * We are testing the service in isolation by mocking its dependencies (ports).
 */
@ExtendWith(MockitoExtension.class)
class RegisterCustomerServiceTest {

    // The @Mock annotation creates a mock implementation for the CustomerRepository.
    // This allows us to control its behavior during tests without needing a real database.
    @Mock
    private CustomerRepository customerRepository;

    // The @InjectMocks annotation creates an instance of RegisterCustomerService
    // and injects the mocks (like customerRepository) into it.
    @InjectMocks
    private RegisterCustomerService registerCustomerService;

    @Test
    void shouldRegisterNewCustomer_whenDataIsValidAndEmailIsUnique() throws ExecutionException, InterruptedException {
        // --- ARRANGE ---

        // 1. Create the input data for our use case.
        RegisterCustomerCommand command = RegisterCustomerCommand.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .cpf("12345678900")
                .build();

        // 2. Define the behavior of our mock repository.
        // When findByEmail is called with this email, it should return an empty Optional,
        // signifying that the email is not yet taken.
        when(customerRepository.findByEmail(command.getEmail()))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // When save is called with any Customer object, it should immediately return
        // a completed CompletableFuture containing that same customer.
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));


        // --- ACT ---

        // 3. Execute the method we are testing.
        CompletableFuture<Customer> future = registerCustomerService.registerCustomer(command);
        Customer createdCustomer = future.get(); // .get() waits for the future to complete

        // --- ASSERT ---

        // 4. Verify the results.
        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.getFirstName()).isEqualTo(command.getFirstName());
        assertThat(createdCustomer.getEmail()).isEqualTo(command.getEmail());

        // 5. Use an ArgumentCaptor to capture the actual Customer object that was passed to the save method.
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture()); // Verify save was called and capture the argument
        Customer savedCustomer = customerCaptor.getValue();

        // 6. Assert that the service correctly set the new fields before saving.
        assertThat(savedCustomer.getCustomerId()).isNotNull().isNotBlank();
        assertThat(savedCustomer.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldThrowException_whenEmailIsAlreadyRegistered() {
        // --- ARRANGE ---
        RegisterCustomerCommand command = RegisterCustomerCommand.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .cpf("00987654321")
                .build();

        Customer existingCustomer = Customer.builder().email(command.getEmail()).build();

        // Configure the mock to find an existing customer with this email.
        when(customerRepository.findByEmail(command.getEmail()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(existingCustomer)));

        // --- ACT & ASSERT ---
        // We expect the call to result in an exception.
        CompletableFuture<Customer> future = registerCustomerService.registerCustomer(command);

        assertThatThrownBy(future::get) // Use future::get to test the result of the async operation
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email already registered");

        // Verify that the save method was NEVER called in this scenario.
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
