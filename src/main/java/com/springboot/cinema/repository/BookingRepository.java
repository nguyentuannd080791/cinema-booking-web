package com.springboot.cinema.repository;

import com.springboot.cinema.entity.Booking;
import com.springboot.cinema.entity.BookingStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer> {
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.bookingStatus = 'PAID'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.bookingStatus = 'PAID' AND b.bookingTime >= :since")
    BigDecimal getRevenueSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = 'USED' OR t.status = 'VALID'")
    long getSoldTicketsCount();

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingStatus = :status")
    long countByBookingStatus(@Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = 'PAID' AND b.bookingTime >= :since")
    List<Booking> findPaidBookingsSince(@Param("since") LocalDateTime since);

    @Query("SELECT DISTINCT b FROM Booking b " +
            "LEFT JOIN FETCH b.customer c LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH b.staff st LEFT JOIN FETCH st.user " +
            "LEFT JOIN FETCH b.ticketList t LEFT JOIN FETCH t.showtime s LEFT JOIN FETCH s.movie " +
            "ORDER BY b.bookingTime DESC")
    List<Booking> findRecentBookings(Pageable pageable);

    @Query("SELECT DISTINCT b FROM Booking b JOIN b.ticketList t " +
            "WHERE b.bookingStatus = :status AND t.showtime.startTime <= :time")
    List<Booking> findByBookingStatusAndTicketList_Showtime_StartTimeBefore(@Param("status") BookingStatus status,
                                                                             @Param("time") LocalDateTime time);

    @Query("SELECT DISTINCT b FROM Booking b " +
            "LEFT JOIN FETCH b.ticketList t " +
            "LEFT JOIN FETCH t.seat " +
            "LEFT JOIN FETCH t.showtime s " +
            "LEFT JOIN FETCH s.movie " +
            "LEFT JOIN FETCH s.room " +
            "LEFT JOIN FETCH b.payment " +
            "WHERE (b.customer.id = :userId OR b.staff.id = :userId) " +
            "ORDER BY b.bookingTime DESC")
    List<Booking> findAllByUserIdOrderByBookingTimeDesc(@Param("userId") int userId);
}
