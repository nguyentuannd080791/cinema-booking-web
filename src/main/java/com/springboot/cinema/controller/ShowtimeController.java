package com.springboot.cinema.controller;

import com.springboot.cinema.dto.SeatListDTO;
import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.entity.Role;
import com.springboot.cinema.entity.Seat;
import com.springboot.cinema.entity.Showtime;
import com.springboot.cinema.service.SeatService;
import com.springboot.cinema.service.ShowtimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ShowtimeController {
    private SeatService seatService;
    private ShowtimeService showtimeService;

    public ShowtimeController(SeatService seatService, ShowtimeService showtimeService) {
        this.seatService = seatService;
        this.showtimeService = showtimeService;
    }

    @GetMapping("/showtime/{id}")
    public String getSeat(Model model,
                          @PathVariable("id") String rawShowtimeId) {
        Integer showtimeId = Integer.parseInt(rawShowtimeId);

        getSeatMap(showtimeId, model);

        return "seat";
    }

    @PostMapping("/showtime/{id}")
    public String getSeat(@PathVariable("id") String rawShowtimeId,
                          @RequestParam("selectedSeatIds") List<Integer> selectedSeatIds,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        if(selectedSeatIds.size() == 0)
        {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa chọn ghế nào");
            return "redirect:/showtime/" + rawShowtimeId;
        }

        Integer showtimeId = Integer.parseInt(rawShowtimeId);

        session.setAttribute("showtimeId", showtimeId);
        session.setAttribute("selectedSeatIds", selectedSeatIds);

        return "redirect:/confirm-booking";
    }

    @GetMapping("/staff/showtime/{id}/check-in")
    public String checkIn(HttpSession session,
                          @PathVariable("id") String rawShowtimeId,
                          Model model)
    {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        if (user == null || user.getRole() != Role.STAFF)
            return "redirect:/home";

        Integer showtimeId = Integer.parseInt(rawShowtimeId);
        getSeatMap(showtimeId, model);
        return "checkin-seat";
    }

    @GetMapping("/staff/showtime/{id}/walk-in")
    public String walkIn(HttpSession session,
                         @PathVariable("id") String rawShowtimeId,
                         Model model)
    {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        if (user == null || user.getRole() != Role.STAFF)
            return "redirect:/home";

        Integer showtimeId = Integer.parseInt(rawShowtimeId);
        getSeatMap(showtimeId, model);
        return "walkin-seat";
    }

    private void getSeatMap(int showtimeId, Model model)
    {
        List<SeatListDTO> seatList = seatService.getAllSeatByShowtimeId(showtimeId);
        Map<Integer, List<SeatListDTO>> seatMap = seatList.stream().collect(Collectors.groupingBy(SeatListDTO::getRowIndex));

        model.addAttribute("seatMap", seatMap);
        model.addAttribute("showtimeId", showtimeId);
    }
}
