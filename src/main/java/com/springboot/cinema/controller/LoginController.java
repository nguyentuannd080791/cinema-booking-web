package com.springboot.cinema.controller;

import com.springboot.cinema.dto.UserInformationDTO;
import com.springboot.cinema.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    private LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        System.out.println(email + "....." + password);
        UserInformationDTO userInformationDTO = loginService.login(email, password);

        if(userInformationDTO == null){
            redirectAttributes.addFlashAttribute("error", "Email hoặc mật khẩu không đúng");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/login";
        }

        session.setAttribute("user", userInformationDTO);
        return "redirect:/home";
    }
}
