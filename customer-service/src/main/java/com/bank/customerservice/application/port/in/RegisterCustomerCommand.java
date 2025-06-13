package com.bank.customerservice.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * A command object to carry the data required to register a new customer.
 * Using a dedicated command object makes the use case explicit about its required inputs.
 */
@Data
@Builder
public class RegisterCustomerCommand {

    @NotBlank(message = "First name cannot be blank")
    private final String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private final String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private final String email;

    @NotBlank(message = "CPF cannot be blank")
    private final String cpf; // Cadastro de Pessoas Físicas (CPF)
}
