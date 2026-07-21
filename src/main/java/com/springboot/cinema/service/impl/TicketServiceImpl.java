package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.BookingStatus;
import com.springboot.cinema.entity.Ticket;
import com.springboot.cinema.entity.TicketStatus;
import com.springboot.cinema.repository.TicketRepository;
import com.springboot.cinema.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public void checkIn(Integer showtimeId, List<Integer> selectedSeatIds) {
        if (selectedSeatIds == null || selectedSeatIds.isEmpty()) {
            throw new IllegalArgumentException("Không có ghế nào được chọn để check-in.");
        }

        List<Ticket> ticketList = (List<Ticket>) ticketRepository.getAllTicketBySeatList(showtimeId, selectedSeatIds);

        if (ticketList.size() < selectedSeatIds.size()) {
            throw new IllegalStateException("Một hoặc nhiều ghế được chọn chưa được đặt vé hoặc không tìm thấy vé hợp lệ.");
        }

        for (Ticket ticket : ticketList) {
            if (ticket.getStatus() == TicketStatus.USED) {
                throw new IllegalStateException("Ghế " + ticket.getSeat().getSeatNumber() + " đã được soát vé (check-in) trước đó.");
            }
            if (ticket.getStatus() == TicketStatus.INVALID) {
                throw new IllegalStateException("Vé cho ghế " + ticket.getSeat().getSeatNumber() + " không hợp lệ.");
            }
            if (ticket.getBooking() == null || ticket.getBooking().getBookingStatus() != BookingStatus.PAID) {
                throw new IllegalStateException("Vé cho ghế " + ticket.getSeat().getSeatNumber() + " chưa được thanh toán, không thể check-in.");
            }
            ticket.setStatus(TicketStatus.USED);
        }

        ticketRepository.saveAll(ticketList);
    }
}
