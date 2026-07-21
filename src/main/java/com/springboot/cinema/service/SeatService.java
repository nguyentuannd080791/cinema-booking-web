package com.springboot.cinema.service;

import com.springboot.cinema.dto.SeatListDTO;
import com.springboot.cinema.entity.Seat;

import java.util.List;

public interface SeatService {
    List<SeatListDTO> getAllSeatByShowtimeId(Integer showtimeId);

    List<SeatListDTO> getCustomerSeatList(Integer showtimeId, List<Integer> selectedSeatIds);

    Double caculateTotalPrice(Integer showtimeId, List<Integer> selectedSeatIds);

    void validateSeatsAreAvailable(Integer showtimeId, List<Integer> selectedSeatIds);

    List<Seat> getSeatsByRoomId(int roomId);

    Integer updateSeatType(int seatId, String seatType);

    void createSeatGrid(int roomId, int rows, int cols);
}