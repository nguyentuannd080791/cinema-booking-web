package com.springboot.cinema.repository;

import com.springboot.cinema.entity.Ticket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends CrudRepository<Ticket, Integer> {
    @Query("SELECT t FROM Ticket t " +
            "WHERE t.showtime.id = :showtimeId AND t.seat.id IN :seatListIds")
    List<Ticket> getAllTicketBySeatList(@Param("showtimeId") Integer showtimeId,
                                        @Param("seatListIds") List<Integer> seatListIds);
}
