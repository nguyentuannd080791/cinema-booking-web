package com.springboot.cinema.controller;

import com.springboot.cinema.dto.SeatListDTO;
import com.springboot.cinema.entity.Seat;
import com.springboot.cinema.service.SeatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ShowtimeController {
    private SeatService seatService;

    public ShowtimeController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/showtime/{id}")
    public String getSeat(Model model,
                          @PathVariable("id") String rawShowtimeId)
    {
        Integer showtimeId = Integer.parseInt(rawShowtimeId);

        List<SeatListDTO> seatList = seatService.getAllSeatByShowtimeId(showtimeId);

        model.addAttribute("seatList", seatList);
        return "seat";
    }
}
