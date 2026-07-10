package com.springboot.cinema.repository;

import com.springboot.cinema.entity.Showtime;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowtimeRepository extends CrudRepository<Showtime, Integer> {
}
