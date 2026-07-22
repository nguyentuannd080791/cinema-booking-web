package com.springboot.cinema.controller;

import com.springboot.cinema.dto.SeatListDTO;
import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.entity.*;
import com.springboot.cinema.service.BookingService;
import com.springboot.cinema.service.SeatService;
import com.springboot.cinema.service.ShowtimeService;
import com.springboot.cinema.service.TicketService;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
public class ShowtimeController {
    private SeatService seatService;
    private ShowtimeService showtimeService;
    private TicketService ticketService;
    private BookingService bookingService;

    public ShowtimeController(SeatService seatService, ShowtimeService showtimeService, TicketService ticketService, BookingService bookingService) {
        this.seatService = seatService;
        this.showtimeService = showtimeService;
        this.ticketService = ticketService;
        this.bookingService = bookingService;
    }

    private Integer parseIdOrNull(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @GetMapping("/showtime/{id}")
    public String getSeat(Model model,
                          @PathVariable("id") String rawShowtimeId,
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole() == Role.STAFF) {
            redirectAttributes.addFlashAttribute("error", "Nhân viên không thể đặt vé.");
            return "redirect:/home";
        }

        Integer showtimeId = parseIdOrNull(rawShowtimeId);
        if (showtimeId == null) {
            return "redirect:/home";
        }

        Showtime showtime = showtimeService.getShowtimeById(showtimeId);
        if (showtime == null || showtime.getStatus() != ShowtimeStatus.OPEN) {
            redirectAttributes.addFlashAttribute("error", "Suất chiếu này chưa được mở bán hoặc không tồn tại.");
            return "redirect:/home";
        }

        getSeatMap(showtimeId, model);

        return "seat";
    }

    private boolean isNotStaff(HttpSession session) {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        return user == null || user.getRole() != Role.STAFF;
    }

    @PostMapping("/showtime/{id}")
    public String getSeat(@PathVariable("id") String rawShowtimeId,
                          @RequestParam("selectedSeatIds") List<Integer> selectedSeatIds,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (selectedSeatIds.size() == 0) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa chọn ghế nào");
            return "redirect:/showtime/" + rawMovieIdOrShowtimeId(rawShowtimeId);
        }

        Integer showtimeId = parseIdOrNull(rawShowtimeId);
        if (showtimeId == null) {
            return "redirect:/home";
        }

        Integer existingBookingId = (Integer) session.getAttribute("bookingId");
        if (existingBookingId != null) {
            Booking existingBooking = bookingService.getBookingById(existingBookingId);
            if (existingBooking != null && existingBooking.getBookingStatus() == BookingStatus.PENDING) {
                redirectAttributes.addFlashAttribute("error", "Bạn đang có một đơn đặt vé chờ thanh toán. Vui lòng hoàn tất hoặc huỷ đơn đó trước khi đặt ghế khác.");
                return "redirect:/confirm-booking";
            }

            session.removeAttribute("bookingId");
        }

        try {

            seatService.validateSeatsAreAvailable(showtimeId, selectedSeatIds);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/showtime/" + showtimeId;
        }

        session.setAttribute("showtimeId", showtimeId);
        session.setAttribute("selectedSeatIds", selectedSeatIds);

        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        if (user == null) {

            return "redirect:/login";
        }

        try {
            Booking booking = bookingService.createBooking(user.getUserId(), showtimeId, selectedSeatIds);
            session.setAttribute("bookingId", booking.getId());
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Đặt vé thất bại: Ghế này đã bị người khác đặt trước mất rồi. Vui lòng chọn ghế khác.");
            session.removeAttribute("showtimeId");
            session.removeAttribute("selectedSeatIds");
            return "redirect:/showtime/" + showtimeId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đặt vé thất bại: " + e.getMessage());
            session.removeAttribute("showtimeId");
            session.removeAttribute("selectedSeatIds");
            return "redirect:/showtime/" + showtimeId;
        }

        return "redirect:/confirm-booking";
    }

    private String rawMovieIdOrShowtimeId(String rawId) {
        return rawId;
    }

    @GetMapping("/staff/showtime/{id}/check-in")
    public String checkIn(HttpSession session,
                          @PathVariable("id") String rawShowtimeId,
                          Model model) {
        if (isNotStaff(session))
            return "redirect:/home";

        Integer showtimeId = parseIdOrNull(rawShowtimeId);
        if (showtimeId == null) {
            return "redirect:/home";
        }
        getSeatMap(showtimeId, model);
        model.addAttribute("showtime", showtimeService.getShowtimeById(showtimeId));
        return "checkin-seat";
    }

    @PostMapping("/staff/showtime/{id}/check-in")
    public String checkIn(HttpSession session,
                          @PathVariable("id") String rawShowtimeId,
                          @RequestParam("selectedSeatIds") List<Integer> selectedSeatIds,
                          RedirectAttributes redirectAttributes) {
        if (isNotStaff(session))
            return "redirect:/home";

        Integer showtimeId = parseIdOrNull(rawShowtimeId);
        if (showtimeId == null) {
            return "redirect:/home";
        }
        try {
            if (selectedSeatIds == null || selectedSeatIds.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn ít nhất một ghế để check-in.");
            }
            ticketService.checkIn(showtimeId, selectedSeatIds);
            redirectAttributes.addFlashAttribute("success", "Check in thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Check in thất bại: " + e.getMessage());
        }

        return "redirect:/staff/showtime/" + showtimeId + "/check-in";
    }

    @GetMapping("/staff/showtime/{id}/walk-in")
    public String walkIn(HttpSession session,
                         @PathVariable("id") String rawShowtimeId,
                         Model model) {
        if (isNotStaff(session))
            return "redirect:/home";

        Integer showtimeId = parseIdOrNull(rawShowtimeId);
        if (showtimeId == null) {
            return "redirect:/home";
        }
        getSeatMap(showtimeId, model);
        model.addAttribute("showtime", showtimeService.getShowtimeById(showtimeId));
        return "walkin-seat";
    }

    @PostMapping("/staff/showtime/{id}/walk-in")
    public String walkIn(HttpSession session,
                         @PathVariable("id") String rawShowtimeId,
                         @RequestParam("selectedSeatIds") List<Integer> selectedSeatIds,
                         RedirectAttributes redirectAttributes)
    {
        if (isNotStaff(session))
            return "redirect:/home";

        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        Integer showtimeId = parseIdOrNull(rawShowtimeId);
        if (showtimeId == null) {
            return "redirect:/home";
        }

        try {
            if (selectedSeatIds == null || selectedSeatIds.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn ít nhất một ghế.");
            }
            bookingService.createBooking(user.getUserId(), showtimeId, selectedSeatIds);
            redirectAttributes.addFlashAttribute("success", "Tạo vé thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Tạo vé thất bại: " + e.getMessage());
        }

        return "redirect:/staff/showtime/" + showtimeId + "/walk-in";
    }

    private void getSeatMap(int showtimeId, Model model) {
        List<SeatListDTO> seatList = seatService.getAllSeatByShowtimeId(showtimeId);
        Map<String, List<SeatListDTO>> seatMap = seatList.stream().collect(Collectors.groupingBy(seat -> {
            if (seat.getSeatNumber() != null && !seat.getSeatNumber().isEmpty()) {
                String seatNum = seat.getSeatNumber();
                int i = 0;
                while (i < seatNum.length() && Character.isLetter(seatNum.charAt(i))) {
                    i++;
                }
                if (i > 0) {
                    return seatNum.substring(0, i);
                }
            }
            return String.valueOf((char)('A' + seat.getRowIndex() - 1));
        }, TreeMap::new, Collectors.toList()));

        model.addAttribute("seatMap", seatMap);
        model.addAttribute("showtimeId", showtimeId);
    }
}
