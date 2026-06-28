package com.springboot.cinema.controller;

import com.springboot.cinema.dto.RegisterFormDTO;
import com.springboot.cinema.service.RegisterService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {
    private RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
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

        if(registerService.isEmailExists(registerFormDTO.getEmail())) {
            result.rejectValue("email", "error.email", "Email này đã tồn tại");
        }

        if (result.hasErrors()) {
            loadForm(model, registerFormDTO);
            return "register";
        }

        registerService.registerUser(registerFormDTO);

        return "redirect:/login";
    }

    private void loadForm(Model model, RegisterFormDTO registerFormDTO) {
        model.addAttribute("registerForm", registerFormDTO);
    }
}
