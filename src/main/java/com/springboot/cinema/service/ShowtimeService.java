package com.springboot.cinema.service;

import com.springboot.cinema.entity.Room;
import com.springboot.cinema.entity.Showtime;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ShowtimeService {
    Showtime getShowtimeById(Integer showtimeId);

    List<LocalDate> getShowtimeDates(int movieId);

    Map<Room, List<Showtime>> getShowtimesByRoomAndDate(int movieId, LocalDate date);

    List<Showtime> getShowtimesByMovieId(int movieId);

    long countSoldTickets(int showtimeId);

    Showtime saveShowtime(Showtime showtime, int movieId, int roomId);

    Showtime openForSale(int showtimeId);

    void deleteShowtime(int showtimeId);
}
