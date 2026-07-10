package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.Showtime;
import com.springboot.cinema.repository.ShowtimeRepository;
import com.springboot.cinema.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class ShowtimeServiceImpl implements ShowtimeService {
    @Autowired
    private ShowtimeRepository showtimeRepository;

    public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository) {
        this.showtimeRepository = showtimeRepository;
    }

    @Override
    public Showtime getShowtimeById(Integer showtimeId) {
        return showtimeRepository.findById(showtimeId).orElse(null);
    }
}
