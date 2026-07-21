package com.springboot.cinema.controller;

import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.entity.Movie;
import com.springboot.cinema.entity.Role;
import com.springboot.cinema.entity.Room;
import com.springboot.cinema.entity.Seat;
import com.springboot.cinema.repository.BookingRepository;
import com.springboot.cinema.service.MovieService;
import com.springboot.cinema.service.RoomService;
import com.springboot.cinema.service.SeatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MovieService movieService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SeatService seatService;

    private boolean isNotAdmin(HttpSession session) {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        return user == null || user.getRole() != Role.ADMIN;
    }

    @GetMapping("/admin/dashboard")
    public String getDashboard(Model model, HttpSession session) {
        if (isNotAdmin(session)) {
            return "redirect:/home";
        }

        BigDecimal revenue = bookingRepository.getTotalRevenue();
        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }
        long ticketCount = bookingRepository.getSoldTicketsCount();

        model.addAttribute("revenue", revenue);
        model.addAttribute("ticketCount", ticketCount);
        return "dashboard";
    }

    @GetMapping("/admin/movies")
    public String getMovies(Model model, HttpSession session) {
        if (isNotAdmin(session)) {
            return "redirect:/home";
        }
        model.addAttribute("movies", movieService.getAllMovies());
        return "crud-movies";
    }

    @GetMapping("/admin/rooms")
    public String getRooms(Model model, HttpSession session) {
        if (isNotAdmin(session)) {
            return "redirect:/home";
        }
        model.addAttribute("rooms", roomService.getAllRooms());
        return "crud-rooms";
    }

    @GetMapping("/admin/seats")
    public String getSeats(@RequestParam(value = "roomId", required = false) Integer roomId,
                           Model model, HttpSession session) {
        if (isNotAdmin(session)) {
            return "redirect:/home";
        }
        List<Room> rooms = roomService.getAllRooms();
        model.addAttribute("rooms", rooms);
        
        if (roomId == null && !rooms.isEmpty()) {
            roomId = rooms.get(0).getId();
        }
        
        if (roomId != null) {
            model.addAttribute("selectedRoomId", roomId);
            List<Seat> seats = seatService.getSeatsByRoomId(roomId);
            
            Map<String, List<Seat>> seatMap = seats.stream().collect(Collectors.groupingBy(seat -> {
                String seatNum = seat.getSeatNumber();
                if (seatNum != null && !seatNum.isEmpty()) {
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
            model.addAttribute("selectedRoom", roomService.getRoomById(roomId));
        }
        return "crud-seats";
    }

    @PostMapping("/admin/movies")
    public String saveMovie(@ModelAttribute Movie movie,
                            RedirectAttributes redirectAttributes) {
        try {
            movieService.saveMovie(movie);
            redirectAttributes.addFlashAttribute("success", "Lưu phim thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lưu phim thất bại: " + e.getMessage());
        }
        return "redirect:/admin/movies";
    }

    @PostMapping("/admin/movies/{id}/delete")
    public String deleteMovie(@PathVariable("id") int id,
                            RedirectAttributes redirectAttributes) {
        try {
            movieService.deleteMovie(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phim thành công (Soft delete)!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa phim thất bại: " + e.getMessage());
        }
        return "redirect:/admin/movies";
    }

    @PostMapping("/admin/rooms")
    public String saveRoom(@ModelAttribute Room room,
                           RedirectAttributes redirectAttributes) {
        try {
            roomService.saveRoom(room);
            redirectAttributes.addFlashAttribute("success", "Lưu phòng chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lưu phòng chiếu thất bại: " + e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @PostMapping("/admin/rooms/{id}/delete")
    public String deleteRoom(@PathVariable("id") int id,
                             RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phòng chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa phòng chiếu thất bại: " + e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @PostMapping("/admin/seats/{id}/type")
    public String updateSeatType(@PathVariable("id") int seatId,
                                 @RequestParam("seatType") String seatType,
                                 RedirectAttributes redirectAttributes) {
        Integer roomId = null;
        try {
            roomId = seatService.updateSeatType(seatId, seatType);
            redirectAttributes.addFlashAttribute("success", "Cập nhật loại ghế thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật loại ghế thất bại: " + e.getMessage());
        }
        return roomId != null ? "redirect:/admin/seats?roomId=" + roomId : "redirect:/admin/seats";
    }

    @PostMapping("/admin/seats/grid")
    public String createSeatGrid(@RequestParam("roomId") int roomId,
                                 @RequestParam("rows") int rows,
                                 @RequestParam("cols") int cols,
                                 RedirectAttributes redirectAttributes) {
        try {
            seatService.createSeatGrid(roomId, rows, cols);
            redirectAttributes.addFlashAttribute("success", "Tạo lưới ghế cho phòng chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Tạo lưới ghế thất bại: " + e.getMessage());
        }
        return "redirect:/admin/seats?roomId=" + roomId;
    }
}
