package com.springboot.cinema.dto;

import com.springboot.cinema.entity.SeatType;
import com.springboot.cinema.entity.TicketStatus;

import java.math.BigDecimal;

public class SeatListDTO {
    private int seatId;
    private BigDecimal price;
    private int colIndex;
    private int rowIndex;
    private String seatNumber;
    private SeatType seatType;
    private TicketStatus ticketStatus;

    public SeatListDTO() {
    }

    public SeatListDTO(int seatId, BigDecimal price, int colIndex, int rowIndex, String seatNumber, SeatType seatType, TicketStatus ticketStatus) {
        this.seatId = seatId;
        this.price = price;
        this.colIndex = colIndex;
        this.rowIndex = rowIndex;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.ticketStatus = ticketStatus;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
}
