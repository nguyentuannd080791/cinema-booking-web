package com.springboot.cinema.utils;

import com.springboot.cinema.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Runs periodically to release seats held by "PENDING" bookings whose showtime has already
 * started (khách chưa thanh toán kịp trước giờ chiếu). Not a short 5-10 minute reservation
 * timeout — just a safeguard so seats aren't locked forever past a showtime that already happened.
 */
@Component
public class ExpiredBookingCleanupJob {

    @Autowired
    private PaymentService paymentService;

    public ExpiredBookingCleanupJob(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Scheduled(fixedRate = 60000)
    public void releaseExpiredPendingBookings() {
        paymentService.releaseExpiredPendingBookings();
    }
}
