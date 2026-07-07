package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.Movie;
import com.springboot.cinema.repository.MovieRepository;
import com.springboot.cinema.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Page<Movie> getMovieList(String categoryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return movieRepository.findByCategoryName(categoryName, pageable);
    }

    @Override
    public Movie getMovieById(int movieId) {
        return movieRepository.findById(movieId).orElse(null);
    }
}
