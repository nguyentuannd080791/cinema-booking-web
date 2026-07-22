package com.springboot.cinema.dto;

import java.math.BigDecimal;

public class TopMovieStatDTO {
    private int movieId;
    private String title;
    private String posterURL;
    private long ticketsSold;
    private BigDecimal revenue;

    public TopMovieStatDTO() {
    }

    public TopMovieStatDTO(int movieId, String title, String posterURL, long ticketsSold, BigDecimal revenue) {
        this.movieId = movieId;
        this.title = title;
        this.posterURL = posterURL;
        this.ticketsSold = ticketsSold;
        this.revenue = revenue;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public long getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(long ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
