package com.springboot.cinema.service.impl;

import com.springboot.cinema.dto.SeatListDTO;
import com.springboot.cinema.entity.SeatType;
import com.springboot.cinema.repository.SeatRepository;
import com.springboot.cinema.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {
    @Autowired
    private SeatRepository seatRepository;

    private static final Double VIP_SEAT_COST_MULTIPLIER = 1.5;
    private static final Double COUPLE_SEAT_COST_MULTIPLIER = 1.2;

    @Override
    public List<SeatListDTO> getAllSeatByShowtimeId(Integer showtimeId) {
        List<SeatListDTO> seatList =  seatRepository.getSeatListByShowtimeId(showtimeId);

        for(SeatListDTO seat : seatList)
        {
            Double cost = seat.getPrice().doubleValue();
            if(seat.getSeatType() == SeatType.VIP) cost *= VIP_SEAT_COST_MULTIPLIER;
            else if(seat.getSeatType() == SeatType.COUPLE) cost *= COUPLE_SEAT_COST_MULTIPLIER;

            seat.setPrice(BigDecimal.valueOf(cost));
        }

        return seatList;
    }
}
