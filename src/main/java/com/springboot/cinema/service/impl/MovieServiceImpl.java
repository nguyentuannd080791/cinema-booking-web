package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.Category;
import com.springboot.cinema.entity.Movie;
import com.springboot.cinema.entity.MovieStatus;
import com.springboot.cinema.repository.CategoryRepository;
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

    @Autowired
    private CategoryRepository categoryRepository;

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
    public Movie saveMovie(Movie movie, List<Integer> categoryIds) {
        List<Category> categories = new ArrayList<>();
        if (categoryIds != null) {
            for (Integer categoryId : categoryIds) {
                categoryRepository.findById(categoryId).ifPresent(categories::add);
            }
        }

        if (movie.getId() != 0) {
            Movie existing = movieRepository.findById(movie.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại."));
            existing.setTitle(movie.getTitle());
            existing.setDuration(movie.getDuration());
            existing.setReleaseDate(movie.getReleaseDate());
            existing.setDescription(movie.getDescription());
            existing.setPosterURL(movie.getPosterURL());
            existing.setLanguage(movie.getLanguage());
            existing.setAgeRating(movie.getAgeRating());
            existing.setStatus(movie.getStatus());
            existing.setCategoryList(categories);
            return movieRepository.save(existing);
        }

        movie.setCategoryList(categories);
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
