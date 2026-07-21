package com.springboot.cinema.utils;

import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        UserInformationDTO user = (UserInformationDTO) session.getAttribute("user");

        String uri = request.getRequestURI();

        if (uri.startsWith("/admin")) {
            if (user == null) {
                response.sendRedirect("/login");
                return false;
            }
            if (user.getRole() != Role.ADMIN) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập vùng quản trị!");
                return false;
            }
        } else if (uri.startsWith("/staff")) {
            if (user == null) {
                response.sendRedirect("/login");
                return false;
            }
            if (user.getRole() != Role.STAFF && user.getRole() != Role.ADMIN) {
                response.sendRedirect("/home");
                return false;
            }
        }

        return true;
    }
}
