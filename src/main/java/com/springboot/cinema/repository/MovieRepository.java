package com.springboot.cinema.repository;

import com.springboot.cinema.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Integer> {
    @Query("SELECT DISTINCT m FROM Movie m\n" +
            "LEFT JOIN m.categoryList c\n" +
            "WHERE (:movieName IS NULL OR m.title LIKE %:movieName%) \n" +
            "AND (:categoryName IS NULL OR c.categoryName = :categoryName) \n" +
            "AND (m.status <> 'NOLONGERSHOWING')")
    Page<Movie> findByCategoryName(@Param("movieName") String movieName,
                                   @Param("categoryName") String categoryName,
                                   Pageable pageable);

}
