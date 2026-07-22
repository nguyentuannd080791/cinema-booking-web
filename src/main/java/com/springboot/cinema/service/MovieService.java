package com.springboot.cinema.service;

import com.springboot.cinema.entity.Movie;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MovieService {
    Page<Movie> getMovieList(String movieName, String categoryName, int page, int size);

    Movie getMovieById(int movieId);

    List<Movie> getAllMovies();

    Movie saveMovie(Movie movie, List<Integer> categoryIds);

    void deleteMovie(int movieId);
}
