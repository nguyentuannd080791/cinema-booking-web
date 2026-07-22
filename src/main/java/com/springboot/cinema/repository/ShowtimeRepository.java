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
    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.startTime >= :start AND s.startTime <= :end AND s.status = 'OPEN'")
    List<Showtime> findByMovieIdAndDate(@Param("movieId") int movieId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.startTime >= :now AND s.status = 'OPEN'")
    List<Showtime> findUpcomingShowtimesByMovieId(@Param("movieId") int movieId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.room.id = :roomId")
    boolean existsByRoomId(@Param("roomId") int roomId);

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId ORDER BY s.startTime ASC")
    List<Showtime> findAllByMovieIdOrderByStartTime(@Param("movieId") int movieId);

    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.room.id = :roomId AND s.id <> :excludeId " +
           "AND s.startTime < :endTime AND s.endTime > :startTime")
    boolean existsOverlappingShowtime(@Param("roomId") int roomId,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime,
                                       @Param("excludeId") int excludeId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.showtime.id = :showtimeId AND t.booking.bookingStatus <> 'CANCELLED'")
    long countActiveBookedTicketsByShowtimeId(@Param("showtimeId") int showtimeId);

    @Query("SELECT COUNT(s) FROM Showtime s WHERE s.status = 'OPEN' AND s.startTime >= :now")
    long countUpcomingOpenShowtimes(@Param("now") LocalDateTime now);
}
