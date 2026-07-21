package com.springboot.cinema.service;

import java.util.List;

public interface TicketService {
    void checkIn(Integer showtimeId, List<Integer> selectedSeatIds);
}
