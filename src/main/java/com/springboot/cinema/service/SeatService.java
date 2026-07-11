package com.springboot.cinema.service;

import com.springboot.cinema.dto.SeatListDTO;

import java.math.BigDecimal;
import java.util.List;

public interface SeatService {
    List<SeatListDTO> getAllSeatByShowtimeId(Integer showtimeId);

    List<SeatListDTO> getCustomerSeatList(Integer showtimeId, List<Integer> selectedSeatIds);

    public Double caculateTotalPrice(Integer showtimeId, List<Integer> selectedSeatIds);
}
