package com.springboot.cinema.controller;

import com.springboot.cinema.entity.Movie;
import com.springboot.cinema.service.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MovieController {
    private MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/movie/{id}")
    public String getMovieInformation(Model model,
                                      @PathVariable("id") String rawMovieId)
    {
        Integer movieId = Integer.parseInt(rawMovieId);

        Movie movie = movieService.getMovieById(movieId);

        model.addAttribute("movie", movie);

        return "movie-detail";
    }
}
