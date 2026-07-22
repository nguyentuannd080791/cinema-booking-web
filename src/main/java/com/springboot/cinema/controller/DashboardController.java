package com.springboot.cinema.controller;

import com.springboot.cinema.dto.TopMovieStatDTO;
import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.entity.Booking;
import com.springboot.cinema.entity.BookingStatus;
import com.springboot.cinema.entity.Movie;
import com.springboot.cinema.entity.MovieStatus;
import com.springboot.cinema.entity.Role;
import com.springboot.cinema.entity.Room;
import com.springboot.cinema.entity.Seat;
import com.springboot.cinema.entity.Showtime;
import com.springboot.cinema.entity.ShowtimeStatus;
import com.springboot.cinema.repository.BookingRepository;
import com.springboot.cinema.repository.CustomerRepository;
import com.springboot.cinema.repository.ShowtimeRepository;
import com.springboot.cinema.repository.TicketRepository;
import com.springboot.cinema.service.CategoryService;
import com.springboot.cinema.service.MovieService;
import com.springboot.cinema.service.RoomService;
import com.springboot.cinema.service.SeatService;
import com.springboot.cinema.service.ShowtimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private CategoryService categoryService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    private boolean isNotAdmin(HttpSession session) {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        return user == null || user.getRole() != Role.ADMIN;
    }

    @GetMapping("/admin/dashboard")
    public String getDashboard(Model model, HttpSession session) {
        if (isNotAdmin(session)) {
            return "redirect:/home";
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        // Core KPIs
        BigDecimal revenue = bookingRepository.getTotalRevenue();
        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }
        long ticketCount = bookingRepository.getSoldTicketsCount();
        BigDecimal revenueToday = bookingRepository.getRevenueSince(today.atStartOfDay());

        model.addAttribute("revenue", revenue);
        model.addAttribute("ticketCount", ticketCount);
        model.addAttribute("revenueToday", revenueToday);

        // Booking status breakdown
        long paidBookings = bookingRepository.countByBookingStatus(BookingStatus.PAID);
        long pendingBookings = bookingRepository.countByBookingStatus(BookingStatus.PENDING);
        long cancelledBookings = bookingRepository.countByBookingStatus(BookingStatus.CANCELLED);
        long totalBookings = paidBookings + pendingBookings + cancelledBookings;
        model.addAttribute("paidBookings", paidBookings);
        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("cancelledBookings", cancelledBookings);
        model.addAttribute("totalBookings", totalBookings);

        // Catalog / capacity overview
        List<Movie> allMovies = movieService.getAllMovies();
        model.addAttribute("nowShowingCount", allMovies.stream()
                .filter(m -> m.getStatus() == MovieStatus.NOWSHOWING).count());
        model.addAttribute("comingSoonCount", allMovies.stream()
                .filter(m -> m.getStatus() == MovieStatus.COMINGSOON).count());
        model.addAttribute("roomCount", roomService.getAllRooms().size());
        model.addAttribute("upcomingShowtimeCount", showtimeRepository.countUpcomingOpenShowtimes(now));
        model.addAttribute("customerCount", customerRepository.count());

        // Revenue trend for the last 7 days (PAID bookings)
        LocalDate startDate = today.minusDays(6);
        List<Booking> recentPaidBookings = bookingRepository.findPaidBookingsSince(startDate.atStartOfDay());
        Map<LocalDate, BigDecimal> revenueByDate = recentPaidBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getBookingTime().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, Booking::getTotalAmount, BigDecimal::add)));
        DateTimeFormatter dayLabelFormatter = DateTimeFormatter.ofPattern("dd/MM");
        List<String> revenueLabels = new ArrayList<>();
        List<BigDecimal> revenueAmounts = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            LocalDate date = startDate.plusDays(i);
            revenueLabels.add(date.format(dayLabelFormatter));
            revenueAmounts.add(revenueByDate.getOrDefault(date, BigDecimal.ZERO));
        }
        model.addAttribute("revenueLabels", revenueLabels);
        model.addAttribute("revenueAmounts", revenueAmounts);

        // Top 5 best-selling movies
        List<TopMovieStatDTO> topMovies = ticketRepository.findTopSellingMovies(PageRequest.of(0, 5));
        model.addAttribute("topMovies", topMovies);

        // 8 most recent bookings
        List<Booking> recentBookings = bookingRepository.findRecentBookings(PageRequest.of(0, 8));
        model.addAttribute("recentBookings", recentBookings);

        return "dashboard";
    }

    @GetMapping("/admin/movies")
    public String getMovies(Model model, HttpSession session) {
        if (isNotAdmin(session)) {
            return "redirect:/home";
        }
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("categories", categoryService.getCategoryList());
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

    @GetMapping("/admin/showtimes")
    public String getShowtimes(@RequestParam(value = "movieId", required = false) Integer movieId,
                               Model model, HttpSession session) {
        if (isNotAdmin(session)) {
            return "redirect:/home";
        }

        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);
        model.addAttribute("rooms", roomService.getAllRooms());

        if (movieId == null && !movies.isEmpty()) {
            movieId = movies.get(0).getId();
        }
        if (movieId != null) {
            model.addAttribute("selectedMovieId", movieId);
            Movie selectedMovie = movieService.getMovieById(movieId);
            model.addAttribute("selectedMovie", selectedMovie);
            List<Showtime> showtimes = showtimeService.getShowtimesByMovieId(movieId);
            LocalDateTime now = LocalDateTime.now();

            Map<Integer, Long> soldTicketsByShowtimeId = showtimes.stream()
                    .collect(Collectors.toMap(Showtime::getId, s -> showtimeService.countSoldTickets(s.getId())));

            Map<Integer, Boolean> pastByShowtimeId = showtimes.stream()
                    .collect(Collectors.toMap(Showtime::getId,
                            s -> s.getStartTime() == null || !s.getStartTime().isAfter(now)));

            Map<Integer, Boolean> openByShowtimeId = showtimes.stream()
                    .collect(Collectors.toMap(Showtime::getId, s -> s.getStatus() == ShowtimeStatus.OPEN));

            Map<Integer, Boolean> editLockedByShowtimeId = showtimes.stream()
                    .collect(Collectors.toMap(Showtime::getId,
                            s -> openByShowtimeId.get(s.getId()) || pastByShowtimeId.get(s.getId())));

            Map<Integer, Boolean> deleteLockedByShowtimeId = showtimes.stream()
                    .collect(Collectors.toMap(Showtime::getId,
                            s -> openByShowtimeId.get(s.getId()) || soldTicketsByShowtimeId.get(s.getId()) > 0));

            model.addAttribute("showtimes", showtimes);
            model.addAttribute("soldTicketsByShowtimeId", soldTicketsByShowtimeId);
            model.addAttribute("pastByShowtimeId", pastByShowtimeId);
            model.addAttribute("openByShowtimeId", openByShowtimeId);
            model.addAttribute("editLockedByShowtimeId", editLockedByShowtimeId);
            model.addAttribute("deleteLockedByShowtimeId", deleteLockedByShowtimeId);
        }
        return "crud-showtimes";
    }

    @PostMapping("/admin/movies")
    public String saveMovie(@ModelAttribute Movie movie,
                            @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
                            RedirectAttributes redirectAttributes) {
        try {
            movieService.saveMovie(movie, categoryIds);
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

    @PostMapping("/admin/showtimes")
    public String saveShowtime(@ModelAttribute Showtime showtime,
                               @RequestParam("movieId") int movieId,
                               @RequestParam("roomId") int roomId,
                               RedirectAttributes redirectAttributes) {
        try {
            showtimeService.saveShowtime(showtime, movieId, roomId);
            redirectAttributes.addFlashAttribute("success", "Lưu suất chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lưu suất chiếu thất bại: " + e.getMessage());
        }
        return "redirect:/admin/showtimes?movieId=" + movieId;
    }

    @PostMapping("/admin/showtimes/{id}/delete")
    public String deleteShowtime(@PathVariable("id") int id,
                                 @RequestParam("movieId") int movieId,
                                 RedirectAttributes redirectAttributes) {
        try {
            showtimeService.deleteShowtime(id);
            redirectAttributes.addFlashAttribute("success", "Xóa suất chiếu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa suất chiếu thất bại: " + e.getMessage());
        }
        return "redirect:/admin/showtimes?movieId=" + movieId;
    }

    @PostMapping("/admin/showtimes/{id}/open")
    public String openShowtimeForSale(@PathVariable("id") int id,
                                      @RequestParam("movieId") int movieId,
                                      RedirectAttributes redirectAttributes) {
        try {
            showtimeService.openForSale(id);
            redirectAttributes.addFlashAttribute("success", "Đã mở bán vé cho suất chiếu!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Mở bán vé thất bại: " + e.getMessage());
        }
        return "redirect:/admin/showtimes?movieId=" + movieId;
    }
}
