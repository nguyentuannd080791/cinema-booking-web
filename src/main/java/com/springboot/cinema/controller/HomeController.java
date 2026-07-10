package com.springboot.cinema.controller;

import com.springboot.cinema.entity.Movie;
import com.springboot.cinema.service.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    private MovieService movieService;

    public HomeController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/home")
    public String home(HttpSession session,
                       Model model,
                       @RequestParam(value = "movie", required = false) String movieName,
                       @RequestParam(value = "category", required = false) String categoryName,
                       @RequestParam(value = "page", required = false, defaultValue = "0") String page,
                       @RequestParam(value = "size", required = false, defaultValue = "10") String size )
    {
        List<Movie> movieList = movieService.getMovieList(movieName, categoryName, Integer.parseInt(page), Integer.parseInt(size)).getContent();
        model.addAttribute("movieList", movieList);
        return "home";
    }
}
