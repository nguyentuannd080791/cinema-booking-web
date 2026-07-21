package com.springboot.cinema.exception;

/**
 * Thrown when a payment state transition is not allowed
 * (e.g. trying to confirm a payment that is already CANCELLED/FAILED).
 */
public class InvalidPaymentStateException extends RuntimeException {
    public InvalidPaymentStateException(String message) {
        super(message);
    }
}
