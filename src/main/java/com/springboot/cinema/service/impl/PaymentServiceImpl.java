package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.Booking;
import com.springboot.cinema.entity.BookingStatus;
import com.springboot.cinema.entity.Payment;
import com.springboot.cinema.entity.PaymentStatus;
import com.springboot.cinema.exception.InvalidPaymentStateException;
import com.springboot.cinema.exception.PaymentOwnershipException;
import com.springboot.cinema.repository.BookingRepository;
import com.springboot.cinema.repository.PaymentRepository;
import com.springboot.cinema.repository.TicketRepository;
import com.springboot.cinema.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TicketRepository ticketRepository;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository, TicketRepository ticketRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public Payment initiatePayment(Booking booking) {
        Payment payment = new Payment(null, booking.getTotalAmount(), LocalDateTime.now(), PaymentStatus.PENDING);
        payment.setBooking(booking);
        payment.setTransactionReference(generateTransactionReference());

        return paymentRepository.save(payment);
    }

    private String generateTransactionReference() {

        return UUID.randomUUID().toString().replace("-", "") + Long.toHexString(SECURE_RANDOM.nextLong());
    }

    @Override
    @Transactional
    public Payment confirmPayment(String transactionReference, Integer requestingUserId) {
        Payment payment = getOwnedPayment(transactionReference, requestingUserId);

        if (payment.getStatus() == PaymentStatus.SUCCESS) {

            return payment;
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidPaymentStateException("Giao dịch không còn ở trạng thái chờ xử lý, không thể xác nhận.");
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentTime(LocalDateTime.now());
        paymentRepository.save(payment);

        Booking booking = payment.getBooking();
        booking.setBookingStatus(BookingStatus.PAID);
        bookingRepository.save(booking);

        return payment;
    }

    @Override
    @Transactional
    public Payment cancelPayment(String transactionReference, Integer requestingUserId) {
        Payment payment = getOwnedPayment(transactionReference, requestingUserId);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidPaymentStateException("Giao dịch không còn ở trạng thái chờ xử lý, không thể huỷ.");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        releaseBooking(payment.getBooking(), BookingStatus.CANCELLED);

        return payment;
    }

    private Payment getOwnedPayment(String transactionReference, Integer requestingUserId) {
        Payment payment = paymentRepository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> new PaymentOwnershipException("Giao dịch không tồn tại hoặc không thuộc về bạn."));

        Booking booking = payment.getBooking();
        boolean owned = booking != null
                && booking.getCustomer() != null
                && requestingUserId != null
                && booking.getCustomer().getId() == requestingUserId;

        if (!owned) {
            throw new PaymentOwnershipException("Giao dịch không tồn tại hoặc không thuộc về bạn.");
        }

        return payment;
    }

    @Override
    @Transactional
    public void releaseExpiredPendingBookings() {
        List<Booking> expiredBookings = bookingRepository
                .findByBookingStatusAndTicketList_Showtime_StartTimeBefore(BookingStatus.PENDING, LocalDateTime.now());

        for (Booking booking : expiredBookings) {
            Payment payment = booking.getPayment();
            if (payment != null && payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }
            releaseBooking(booking, BookingStatus.CANCELLED);
        }
    }

    private void releaseBooking(Booking booking, BookingStatus newStatus) {
        booking.setBookingStatus(newStatus);
        bookingRepository.save(booking);

        if (booking.getTicketList() != null && !booking.getTicketList().isEmpty()) {
            ticketRepository.deleteAll(booking.getTicketList());
        }
    }
}
