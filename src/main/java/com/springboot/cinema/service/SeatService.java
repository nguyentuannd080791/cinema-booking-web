package com.springboot.cinema.service;

import com.springboot.cinema.dto.SeatListDTO;

import java.util.List;

public interface SeatService {
    List<SeatListDTO> getAllSeatByShowtimeId(Integer showtimeId);
}
