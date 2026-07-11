package com.springboot.cinema.controller;

import com.springboot.cinema.dto.SeatListDTO;
import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.entity.Showtime;
import com.springboot.cinema.service.BookingService;
import com.springboot.cinema.service.SeatService;
import com.springboot.cinema.service.ShowtimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BookingController {
    private ShowtimeService showtimeService;
    private SeatService seatService;
    private BookingService bookingService;

    public BookingController(ShowtimeService showtimeService, SeatService seatService, BookingService bookingService) {
        this.showtimeService = showtimeService;
        this.seatService = seatService;
        this.bookingService = bookingService;
    }

    @GetMapping("/confirm-booking")
    public String displayBooking(Model model,
                                 HttpSession session) {
        Integer showtimeId = (Integer) session.getAttribute("showtimeId");
        List<Integer> selectedSeatIds = (List<Integer>) session.getAttribute("selectedSeatIds");

        Showtime showtime = showtimeService.getShowtimeById(showtimeId);
        List<SeatListDTO> customerSeatList = seatService.getCustomerSeatList(showtimeId, selectedSeatIds);
        Double totalPrice = seatService.caculateTotalPrice(showtimeId, selectedSeatIds);

        model.addAttribute("showtime", showtime);
        model.addAttribute("selectedSeatList", customerSeatList);
        model.addAttribute("totalPrice", totalPrice);

        return "confirm-booking";
    }

    @PostMapping("/confirm-booking")
    public String saveBooking(Model model,
                              HttpSession session) {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Integer showtimeId = (Integer) session.getAttribute("showtimeId");

        List<Integer> selectedSeatIds = (List<Integer>) session.getAttribute("selectedSeatIds");

        bookingService.createBooking(user.getUserId(), showtimeId, selectedSeatIds);
        return "home";

    }
}
