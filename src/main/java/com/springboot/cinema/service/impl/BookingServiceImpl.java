package com.springboot.cinema.service.impl;

import com.springboot.cinema.dto.SeatListDTO;
import com.springboot.cinema.entity.*;
import com.springboot.cinema.repository.*;
import com.springboot.cinema.service.BookingService;
import com.springboot.cinema.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private TicketRepository ticketRepository;

    public BookingServiceImpl(SeatService seatService, SeatRepository seatRepository, UserRepository userRepository, BookingRepository bookingRepository, ShowtimeRepository showtimeRepository, TicketRepository ticketRepository) {
        this.seatService = seatService;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.ticketRepository = ticketRepository;
    }

    private static final Double VIP_SEAT_COST_MULTIPLIER = 1.5;
    private static final Double COUPLE_SEAT_COST_MULTIPLIER = 1.2;

    @Override
    public void createBooking(Integer userId, Integer showtimeId, List<Integer> selectedSeatIds) {
        User user = userRepository.findById(userId).orElse(null);
        Showtime showtime = showtimeRepository.findById(showtimeId).orElse(null);

        Booking booking = saveBooking(user, showtime, selectedSeatIds);

        saveTickets(booking, showtime, selectedSeatIds);
    }

    private Booking saveBooking(User user, Showtime showtime, List<Integer> selectedSeatIds) {
        BigDecimal totalPrice = BigDecimal.valueOf(seatService.caculateTotalPrice(showtime.getId(), selectedSeatIds));

        Booking booking = new Booking(LocalDateTime.now(), BookingStatus.PENDING, totalPrice);
        booking.setCustomer(user.getCustomer());
        bookingRepository.save(booking);

        return booking;
    }

    private void saveTickets(Booking booking, Showtime showtime, List<Integer> selectedSeatIds) {
        List<Seat> seatList = (List<Seat>) seatRepository.findAllById(selectedSeatIds);
        BigDecimal baseCost = showtime.getPrice();

        List<Ticket> ticketList = new ArrayList<>();

        for (Seat seat : seatList) {
            Ticket ticket = new Ticket(TicketStatus.VALID, calculateFinalSeatPrice(baseCost, seat.getSeatType()));

            ticket.setShowtime(showtime);
            ticket.setBooking(booking);
            ticket.setSeat(seat);

            ticketList.add(ticket);
        }

        ticketRepository.saveAll(ticketList);
    }

    private BigDecimal calculateFinalSeatPrice(BigDecimal baseCost, SeatType type) {
        if (type == SeatType.VIP) {
            return baseCost.multiply(BigDecimal.valueOf(VIP_SEAT_COST_MULTIPLIER));
        }
        if (type == SeatType.COUPLE) {
            return baseCost.multiply(BigDecimal.valueOf(COUPLE_SEAT_COST_MULTIPLIER));
        }
        return baseCost;
    }
}
