package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.Movie;
import com.springboot.cinema.entity.MovieStatus;
import com.springboot.cinema.repository.MovieRepository;
import com.springboot.cinema.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Page<Movie> getMovieList(String movieName, String categoryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return movieRepository.findByCategoryName(movieName, categoryName, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Movie getMovieById(int movieId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie != null) {
            movie.getShowtimeList().size();
            movie.getCategoryList().size();
            movie.getActorList().size();
        }
        return movie;
     }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        movieRepository.findAll().forEach(movies::add);
        return movies;
    }

    @Override
    @Transactional
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    @Transactional
    public void deleteMovie(int movieId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie != null) {
            movie.setStatus(MovieStatus.NOLONGERSHOWING);
            movieRepository.save(movie);
        }
    }
}
