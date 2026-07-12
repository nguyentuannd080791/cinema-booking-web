package com.springboot.cinema.service.impl;

import com.springboot.cinema.entity.Showtime;
import com.springboot.cinema.entity.Ticket;
import com.springboot.cinema.entity.TicketStatus;
import com.springboot.cinema.repository.TicketRepository;
import com.springboot.cinema.service.ShowtimeService;
import com.springboot.cinema.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private ShowtimeService showtimeService;
    @Autowired
    private TicketRepository ticketRepository;

    public TicketServiceImpl(ShowtimeService showtimeService, TicketRepository ticketRepository) {
        this.showtimeService = showtimeService;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void checkIn(Integer showtimeId, List<Integer> selectedSeatIds) {
        Showtime showtime = showtimeService.getShowtimeById(showtimeId);

        List<Ticket> ticketList = (List<Ticket>) ticketRepository.getAllTicketBySeatList(showtimeId, selectedSeatIds);

        for (Ticket ticket : ticketList) {
            if(ticket.getStatus() == TicketStatus.VALID)
                ticket.setStatus(TicketStatus.USED);
        }

        ticketRepository.saveAll(ticketList);
    }
}
