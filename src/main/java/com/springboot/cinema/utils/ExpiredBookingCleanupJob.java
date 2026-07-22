package com.springboot.cinema.utils;

import com.springboot.cinema.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
