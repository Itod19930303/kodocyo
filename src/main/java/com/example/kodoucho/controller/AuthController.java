package com.example.kodoucho.controller;

import com.example.kodoucho.dto.RegisterForm;
import com.example.kodoucho.entity.User;
import com.example.kodoucho.service.EmailVerificationService;
import com.example.kodoucho.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @GetMapping("/login")
    public String showLogin() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterForm form, BindingResult result,
                           Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            User user = userService.register(form);
            String sentTo = emailVerificationService.sendVerificationEmail(user);
            request.getSession(true).setAttribute("TWO_FA_EMAIL_SENT_TO", sentTo);
            return "redirect:/auth/email-sent";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }
}
