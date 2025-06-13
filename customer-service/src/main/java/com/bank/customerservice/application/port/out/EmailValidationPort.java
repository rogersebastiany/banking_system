package com.bank.customerservice.application.port.out;

/**
 * Outgoing port for performing advanced email validation checks.
 */
public interface EmailValidationPort {

    /**
     * Checks if the domain of the given email is allowed.
     * @param email The email address to check.
     * @return true if the email domain is allowed, false otherwise.
     */
    boolean isEmailDomainAllowed(String email);
}
