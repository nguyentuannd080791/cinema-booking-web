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
    @Query("SELECT m FROM Movie m\n" +
            "JOIN m.categoryList c\n" +
            "WHERE (:name IS NULL OR c.categoryName LIKE :name)")
    Page<Movie> findByCategoryName(@Param("name") String categoryName,
                                   Pageable pageable);

}
