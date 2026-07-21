package com.springboot.cinema.service.impl;

import com.springboot.cinema.dto.SeatListDTO;
import com.springboot.cinema.entity.Room;
import com.springboot.cinema.entity.Seat;
import com.springboot.cinema.entity.SeatType;
import com.springboot.cinema.entity.Ticket;
import com.springboot.cinema.entity.TicketStatus;
import com.springboot.cinema.repository.RoomRepository;
import com.springboot.cinema.repository.SeatRepository;
import com.springboot.cinema.repository.ShowtimeRepository;
import com.springboot.cinema.repository.TicketRepository;
import com.springboot.cinema.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    private static final Double VIP_SEAT_COST_MULTIPLIER = 1.5;
    private static final Double COUPLE_SEAT_COST_MULTIPLIER = 1.2;

    @Override
    public void validateSeatsAreAvailable(Integer showtimeId, List<Integer> selectedSeatIds) {
        if (selectedSeatIds == null || selectedSeatIds.isEmpty()) {
            throw new IllegalArgumentException("Không có ghế nào được chọn.");
        }
        List<Ticket> existingTickets = ticketRepository.getAllTicketBySeatList(showtimeId, selectedSeatIds);
        for (Ticket ticket : existingTickets) {
            if (ticket.getStatus() != TicketStatus.INVALID) {
                throw new IllegalStateException("Ghế " + ticket.getSeat().getSeatNumber() + " đã được đặt.");
            }
        }
    }

    @Override
    public List<SeatListDTO> getAllSeatByShowtimeId(Integer showtimeId) {
        List<SeatListDTO> seatList = seatRepository.getSeatListByShowtimeId(showtimeId);

        for (SeatListDTO seat : seatList) {
            caculatedSeatCost(seat);
        }

        return seatList;
    }

    @Override
    public List<SeatListDTO> getCustomerSeatList(Integer showtimeId, List<Integer> selectedSeatIds) {
        List<SeatListDTO> seatList = seatRepository.getSeatListByShowtimeId(showtimeId);
        List<SeatListDTO> customerSeatList = new ArrayList<>();

        for (SeatListDTO seat : seatList) {
            if (selectedSeatIds.contains(seat.getSeatId())) {
                caculatedSeatCost(seat);
                customerSeatList.add(seat);
            }
        }

        return customerSeatList;
    }

    @Override
    public Double caculateTotalPrice(Integer showtimeId, List<Integer> selectedSeatIds) {
        List<SeatListDTO> seatList = seatRepository.getSeatListByShowtimeId(showtimeId);
        Double totalPrice = Double.valueOf(0);

        for (SeatListDTO seat : seatList) {
            if (selectedSeatIds.contains(seat.getSeatId())) {
                caculatedSeatCost(seat);

                totalPrice += seat.getPrice().doubleValue();
            }
        }

        return totalPrice;
    }

    private void caculatedSeatCost(SeatListDTO seat) {
        BigDecimal cost = seat.getPrice();

        if (seat.getSeatType() == SeatType.VIP) {
            cost = cost.multiply(BigDecimal.valueOf(VIP_SEAT_COST_MULTIPLIER));
        } else if (seat.getSeatType() == SeatType.COUPLE) {
            cost = cost.multiply(BigDecimal.valueOf(COUPLE_SEAT_COST_MULTIPLIER));
        }

        seat.setPrice(cost);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> getSeatsByRoomId(int roomId) {
        return seatRepository.findByRoomId(roomId);
    }

    @Override
    @Transactional
    public Integer updateSeatType(int seatId, String seatType) {
        Seat seat = seatRepository.findById(seatId).orElse(null);
        if (seat != null) {
            seat.setSeatType(SeatType.valueOf(seatType.toUpperCase()));
            seatRepository.save(seat);
            return seat.getRoom() != null ? seat.getRoom().getId() : null;
        }
        return null;
    }

    @Override
    @Transactional
    public void createSeatGrid(int roomId, int rows, int cols) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            throw new IllegalArgumentException("Không tìm thấy phòng chiếu với ID: " + roomId);
        }

        if (showtimeRepository.existsByRoomId(roomId)) {
            throw new IllegalStateException("Không thể tạo lại sơ đồ ghế: phòng chiếu này đã có suất chiếu được lên lịch. " +
                    "Vui lòng xoá/di chuyển các suất chiếu liên quan trước khi tạo lại lưới ghế.");
        }

        // Delete existing seats in the room first
        List<Seat> existingSeats = seatRepository.findByRoomId(roomId);
        seatRepository.deleteAll(existingSeats);

        // Generate new grid
        List<Seat> newSeats = new ArrayList<>();
        for (int r = 1; r <= rows; r++) {
            char rowLetter = (char) ('A' + r - 1);
            for (int c = 1; c <= cols; c++) {
                String seatNumber = String.valueOf(rowLetter) + c;
                Seat seat = new Seat(seatNumber, r, c, SeatType.NORMAL);
                seat.setRoom(room);
                newSeats.add(seat);
            }
        }
        seatRepository.saveAll(newSeats);

        // Update room capacity & grid dims
        room.setRow(rows);
        room.setCol(cols);
        room.setCapacity(rows * cols);
        roomRepository.save(room);
    }
}
