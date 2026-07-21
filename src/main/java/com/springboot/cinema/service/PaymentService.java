package com.springboot.cinema.service;

import com.springboot.cinema.entity.Booking;
import com.springboot.cinema.entity.Payment;

public interface PaymentService {
    Payment initiatePayment(Booking booking);

    Payment confirmPayment(String transactionReference, Integer requestingUserId);

    Payment cancelPayment(String transactionReference, Integer requestingUserId);

    void releaseExpiredPendingBookings();
}
