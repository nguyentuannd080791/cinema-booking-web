package com.springboot.cinema.controller;

import com.springboot.cinema.dto.SeatListDTO;
import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.entity.Booking;
import com.springboot.cinema.entity.Showtime;
import com.springboot.cinema.service.BookingService;
import com.springboot.cinema.service.SeatService;
import com.springboot.cinema.service.ShowtimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /**
     * Chỉ đọc: không tạo Booking/Ticket ở đây. Quyết định hiển thị màn "Xác nhận giữ ghế"
     * (chưa có bookingId) hay màn "thanh toán" (đã có bookingId).
     */
    @GetMapping("/confirm-booking")
    public String displayBooking(Model model,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Integer showtimeId = (Integer) session.getAttribute("showtimeId");
        List<Integer> selectedSeatIds = (List<Integer>) session.getAttribute("selectedSeatIds");

        if (showtimeId == null || selectedSeatIds == null || selectedSeatIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn suất chiếu và ghế trước khi xác nhận đặt vé.");
            return "redirect:/home";
        }

        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Showtime showtime = showtimeService.getShowtimeById(showtimeId);
        if (showtime == null) {
            redirectAttributes.addFlashAttribute("error", "Suất chiếu không tồn tại.");
            return "redirect:/home";
        }

        model.addAttribute("showtime", showtime);

        Integer bookingId = (Integer) session.getAttribute("bookingId");
        if (bookingId == null) {
            // Màn "Xác nhận giữ ghế": chỉ tóm tắt ghế/giá, chưa ghi dữ liệu gì.
            List<SeatListDTO> customerSeatList = seatService.getCustomerSeatList(showtimeId, selectedSeatIds);
            Double totalPrice = seatService.caculateTotalPrice(showtimeId, selectedSeatIds);

            model.addAttribute("selectedSeatList", customerSeatList);
            model.addAttribute("totalPrice", totalPrice);

            return "confirm-booking";
        }

        // Đã có bookingId: hiển thị màn thanh toán.
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            session.removeAttribute("bookingId");
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn đặt vé.");
            return "redirect:/home";
        }

        model.addAttribute("booking", booking);
        model.addAttribute("payment", booking.getPayment());

        return "confirm-booking";
    }

    /**
     * Điểm ghi dữ liệu thay cho POST /confirm-booking trước đây: tạo Booking khi người dùng
     * vừa đăng nhập xong (đã có lựa chọn ghế trong session nhưng chưa có bookingId).
     */
    @PostMapping("/confirm-booking/create")
    public String createBooking(HttpSession session,
                                RedirectAttributes redirectAttributes) {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Integer showtimeId = (Integer) session.getAttribute("showtimeId");
        List<Integer> selectedSeatIds = (List<Integer>) session.getAttribute("selectedSeatIds");

        if (showtimeId == null || selectedSeatIds == null || selectedSeatIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin đặt vé trong phiên làm việc.");
            return "redirect:/home";
        }

        if (session.getAttribute("bookingId") != null) {
            // Đã có booking rồi (ví dụ do F5/double click) — không tạo trùng, chỉ hiển thị lại màn thanh toán.
            return "redirect:/confirm-booking";
        }

        try {
            Booking booking = bookingService.createBooking(user.getUserId(), showtimeId, selectedSeatIds);
            session.setAttribute("bookingId", booking.getId());
            return "redirect:/confirm-booking";
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
    }

    /**
     * Trang "Đơn đặt vé của tôi": liệt kê toàn bộ đơn (đang chờ/đã thanh toán/đã huỷ) của
     * người dùng đang đăng nhập, mới nhất trước. Chỉ đọc, không thao tác lên session đặt vé hiện tại.
     */
    @GetMapping("/my-bookings")
    public String myBookings(Model model, HttpSession session) {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Booking> bookings = bookingService.getBookingsByUserId(user.getUserId());
        model.addAttribute("bookings", bookings);
        model.addAttribute("user", user);

        return "my-bookings";
    }
}
