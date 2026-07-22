package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.*;
import com.springboot.cinema.repository.*;
import com.springboot.cinema.service.BookingService;
import com.springboot.cinema.service.PaymentService;
import com.springboot.cinema.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private PaymentService paymentService;

    public BookingServiceImpl(SeatService seatService, SeatRepository seatRepository, UserRepository userRepository, BookingRepository bookingRepository, ShowtimeRepository showtimeRepository, TicketRepository ticketRepository, PaymentService paymentService) {
        this.seatService = seatService;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.ticketRepository = ticketRepository;
        this.paymentService = paymentService;
    }

    private static final Double VIP_SEAT_COST_MULTIPLIER = 1.5;
    private static final Double COUPLE_SEAT_COST_MULTIPLIER = 1.2;

    @Override
    @Transactional
    public Booking createBooking(Integer userId, Integer showtimeId, List<Integer> selectedSeatIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng."));
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy suất chiếu."));

        if (showtime.getStatus() != ShowtimeStatus.OPEN) {
            throw new IllegalStateException("Suất chiếu này chưa được mở bán, vui lòng chọn suất chiếu khác.");
        }

        if (selectedSeatIds == null || selectedSeatIds.isEmpty()) {
            throw new IllegalArgumentException("Không có ghế nào được chọn.");
        }

        List<Integer> distinctSeatIds = selectedSeatIds.stream().distinct().collect(Collectors.toList());
        if (distinctSeatIds.size() != selectedSeatIds.size()) {
            throw new IllegalArgumentException("Danh sách ghế chứa giá trị trùng lặp.");
        }

        int roomId = showtime.getRoom().getId();
        List<Seat> verifiedSeats = seatRepository.findByRoomIdAndIdIn(roomId, distinctSeatIds);
        if (verifiedSeats.size() != distinctSeatIds.size()) {
            throw new IllegalArgumentException("Một hoặc nhiều ghế không thuộc phòng chiếu của suất chiếu này hoặc không tồn tại.");
        }

        seatService.validateSeatsAreAvailable(showtimeId, distinctSeatIds);

        Booking booking = saveBooking(user, showtime, distinctSeatIds);

        saveTickets(booking, showtime, verifiedSeats);

        if (booking.getBookingStatus() == BookingStatus.PENDING) {
            paymentService.initiatePayment(booking);
        }

        return booking;
    }

    @Override
    public Booking getBookingById(Integer bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    @Override
    public List<Booking> getBookingsByUserId(Integer userId) {
        return bookingRepository.findAllByUserIdOrderByBookingTimeDesc(userId);
    }

    private Booking saveBooking(User user, Showtime showtime, List<Integer> selectedSeatIds) {
        BigDecimal totalPrice = BigDecimal.valueOf(seatService.caculateTotalPrice(showtime.getId(), selectedSeatIds));

        Booking booking = new Booking(LocalDateTime.now(), BookingStatus.PENDING, totalPrice);

        if(user.getRole() == Role.CUSTOMER) booking.setCustomer(user.getCustomer());
        else if(user.getRole() == Role.STAFF) {
            booking.setStaff(user.getStaff());
            booking.setBookingStatus(BookingStatus.PAID);
        }

        bookingRepository.save(booking);

        return booking;
    }

    private void saveTickets(Booking booking, Showtime showtime, List<Seat> seatList) {
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
