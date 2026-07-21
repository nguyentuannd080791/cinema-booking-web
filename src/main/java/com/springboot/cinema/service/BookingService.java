package com.springboot.cinema.service;

import com.springboot.cinema.entity.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Integer userId, Integer showtimeId, List<Integer> selectedSeatIds);

    Booking getBookingById(Integer bookingId);

    List<Booking> getBookingsByUserId(Integer userId);
}
