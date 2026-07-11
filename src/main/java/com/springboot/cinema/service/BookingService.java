package com.springboot.cinema.service;

import java.util.List;

public interface BookingService {
    void createBooking(Integer userId, Integer showtimeId, List<Integer> selectedSeatIds);
}
