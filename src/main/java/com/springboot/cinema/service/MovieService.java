package com.springboot.cinema.service;

import com.springboot.cinema.entity.Movie;
import org.springframework.data.domain.Page;


public interface MovieService {
    Page<Movie> getMovieList(String categoryName, int page, int size);

    Movie getMovieById(int movieId);
}
