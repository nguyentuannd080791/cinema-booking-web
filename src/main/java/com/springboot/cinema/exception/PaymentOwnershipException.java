package com.springboot.cinema.exception;

public class PaymentOwnershipException extends RuntimeException {
    public PaymentOwnershipException(String message) {
        super(message);
    }
}
