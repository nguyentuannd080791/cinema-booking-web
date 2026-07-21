package com.springboot.cinema.controller;

import com.springboot.cinema.dto.RegisterFormDTO;
import com.springboot.cinema.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {
    private UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerUser(
            Model model
    ) {
        loadForm(model, new RegisterFormDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("registerForm") RegisterFormDTO registerFormDTO,
            BindingResult result,
            @RequestParam("confirm-password") String confirmPassword,
            @RequestParam(value = "checkbox", required = false) String checkbox,
            Model model
    ) {
        if (checkbox == null) {
            result.reject("checkbox", "Chưa đồng ý với điều khoản ứng dụng");
        }
        if (!registerFormDTO.getHashPassword().equals(confirmPassword)) {
            result.rejectValue("hashPassword", "error.hashPassword","Mật khẩu xác nhận không khớp");
        }

        if(userService.isEmailExists(registerFormDTO.getEmail())) {
            result.rejectValue("email", "error.email", "Email này đã tồn tại");
        }

        if (result.hasErrors()) {
            loadForm(model, registerFormDTO);
            return "register";
        }

        userService.registerUser(registerFormDTO);

        return "redirect:/login";
    }

    private void loadForm(Model model, RegisterFormDTO registerFormDTO) {
        model.addAttribute("registerForm", registerFormDTO);
    }
}
