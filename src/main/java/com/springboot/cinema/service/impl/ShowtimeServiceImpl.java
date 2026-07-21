package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.Room;
import com.springboot.cinema.entity.Showtime;
import com.springboot.cinema.repository.ShowtimeRepository;
import com.springboot.cinema.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<LocalDate> getShowtimeDates(int movieId) {
        List<Showtime> showtimes = showtimeRepository.findUpcomingShowtimesByMovieId(movieId, LocalDateTime.now());
        return showtimes.stream()
                .map(s -> s.getStartTime().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Map<Room, List<Showtime>> getShowtimesByRoomAndDate(int movieId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<Showtime> showtimes = showtimeRepository.findByMovieIdAndDate(movieId, start, end);
        return showtimes.stream()
                .collect(Collectors.groupingBy(Showtime::getRoom));
    }
}
