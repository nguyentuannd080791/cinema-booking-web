package com.springboot.cinema.repository;

import com.springboot.cinema.entity.Showtime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends CrudRepository<Showtime, Integer> {
    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.startTime >= :start AND s.startTime <= :end")
    List<Showtime> findByMovieIdAndDate(@Param("movieId") int movieId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.startTime >= :now")
    List<Showtime> findUpcomingShowtimesByMovieId(@Param("movieId") int movieId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.room.id = :roomId")
    boolean existsByRoomId(@Param("roomId") int roomId);
}
