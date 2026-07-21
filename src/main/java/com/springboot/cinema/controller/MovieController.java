package com.springboot.cinema.controller;

import com.springboot.cinema.entity.Movie;
import com.springboot.cinema.entity.Room;
import com.springboot.cinema.entity.Showtime;
import com.springboot.cinema.service.MovieService;
import com.springboot.cinema.service.ShowtimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class MovieController {
    private MovieService movieService;
    private ShowtimeService showtimeService;

    public MovieController(MovieService movieService, ShowtimeService showtimeService) {
        this.movieService = movieService;
        this.showtimeService = showtimeService;
    }

    @GetMapping("/movie/{id}")
    public String getMovieInformation(HttpSession session,
                                      Model model,
                                      @PathVariable("id") String rawMovieId,
                                      @RequestParam(value = "date", required = false) String rawDate) {
        Integer movieId;
        try {
            movieId = Integer.parseInt(rawMovieId);
        } catch (NumberFormatException e) {
            return "redirect:/home";
        }

        Movie movie = movieService.getMovieById(movieId);
        if (movie == null) {
            return "redirect:/home";
        }

        List<LocalDate> showtimeDates = showtimeService.getShowtimeDates(movieId);
        LocalDate selectedDate = null;

        if (rawDate != null && !rawDate.trim().isEmpty()) {
            try {
                selectedDate = LocalDate.parse(rawDate);
            } catch (Exception e) {
                // Ignore parse errors
            }
        }

        if (selectedDate == null) {
            if (!showtimeDates.isEmpty()) {
                selectedDate = showtimeDates.get(0);
            } else {
                selectedDate = LocalDate.now();
            }
        }

        Map<Room, List<Showtime>> showtimesByRoom =
                showtimeService.getShowtimesByRoomAndDate(movieId, selectedDate);

        model.addAttribute("movie", movie);
        model.addAttribute("showtimeDates", showtimeDates);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("showtimesByRoom", showtimesByRoom);
        model.addAttribute("user", session.getAttribute("user"));

        return "movie-detail";
    }
}
