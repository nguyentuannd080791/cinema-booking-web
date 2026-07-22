package com.springboot.cinema.controller;

import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.entity.Booking;
import com.springboot.cinema.exception.InvalidPaymentStateException;
import com.springboot.cinema.exception.PaymentOwnershipException;
import com.springboot.cinema.service.BookingService;
import com.springboot.cinema.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PaymentController {
    private PaymentService paymentService;
    private BookingService bookingService;

    public PaymentController(PaymentService paymentService, BookingService bookingService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
    }

    @PostMapping("/payment/confirm")
    public String confirm(@RequestParam("transactionReference") String transactionReference,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String mismatchRedirect = requireMatchesSessionBooking(transactionReference, session, redirectAttributes);
        if (mismatchRedirect != null) {
            return mismatchRedirect;
        }

        try {

            paymentService.confirmPayment(transactionReference, user.getUserId());

            clearBookingSession(session);
            redirectAttributes.addFlashAttribute("success", "Xác nhận thanh toán thành công!");
            return "redirect:/home";
        } catch (PaymentOwnershipException e) {
            redirectAttributes.addFlashAttribute("error", "Giao dịch không thuộc về bạn.");
            return "redirect:/home";
        } catch (InvalidPaymentStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/confirm-booking";
        }
    }

    @PostMapping("/payment/cancel")
    public String cancel(@RequestParam("transactionReference") String transactionReference,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String mismatchRedirect = requireMatchesSessionBooking(transactionReference, session, redirectAttributes);
        if (mismatchRedirect != null) {
            return mismatchRedirect;
        }

        try {
            paymentService.cancelPayment(transactionReference, user.getUserId());

            clearBookingSession(session);
            redirectAttributes.addFlashAttribute("success", "Đã huỷ đơn đặt vé.");
            return "redirect:/home";
        } catch (PaymentOwnershipException e) {
            redirectAttributes.addFlashAttribute("error", "Giao dịch không thuộc về bạn.");
            return "redirect:/home";
        } catch (InvalidPaymentStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/confirm-booking";
        }
    }

    private String requireMatchesSessionBooking(String transactionReference, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer sessionBookingId = (Integer) session.getAttribute("bookingId");
        if (sessionBookingId == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn đặt vé đang xử lý trong phiên làm việc.");
            return "redirect:/home";
        }

        Booking sessionBooking = bookingService.getBookingById(sessionBookingId);
        boolean matches = sessionBooking != null
                && sessionBooking.getPayment() != null
                && sessionBooking.getPayment().getTransactionReference() != null
                && sessionBooking.getPayment().getTransactionReference().equals(transactionReference);

        if (!matches) {
            redirectAttributes.addFlashAttribute("error", "Giao dịch không khớp với đơn đặt vé đang mở trong phiên làm việc.");
            return "redirect:/home";
        }

        return null;
    }

    private void clearBookingSession(HttpSession session) {
        session.removeAttribute("bookingId");
        session.removeAttribute("showtimeId");
        session.removeAttribute("selectedSeatIds");
    }
}
