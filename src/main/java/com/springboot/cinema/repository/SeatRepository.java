package com.springboot.cinema.repository;

import com.springboot.cinema.dto.SeatListDTO;
import com.springboot.cinema.entity.Seat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends CrudRepository<Seat, Integer> {
    @Query("SELECT new com.springboot.cinema.dto.SeatListDTO(" +
            "s.id, sh.price, s.colIndex, s.rowIndex, s.seatNumber, s.seatType, t.status)\n" +
            "FROM Showtime sh\n" +
            "INNER JOIN Seat s ON sh.room = s.room\n" +
            "LEFT JOIN Ticket t ON t.seat.id = s.id AND t.showtime.id = sh.id\n" +
            "WHERE sh.id = :showtimeId")
    List<SeatListDTO> getSeatListByShowtimeId(@Param("showtimeId") int showtimeId);
}
