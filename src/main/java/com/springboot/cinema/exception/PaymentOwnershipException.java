package com.springboot.cinema.exception;

/**
 * Thrown when a user attempts to confirm or cancel a payment that does not belong to them,
 * or when the transaction reference cannot be found. The message is intentionally generic
 * so callers do not leak whether the failure was "not found" or "not yours".
 */
public class PaymentOwnershipException extends RuntimeException {
    public PaymentOwnershipException(String message) {
        super(message);
    }
}
