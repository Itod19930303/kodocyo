package com.example.kodoucho.controller;

import com.example.kodoucho.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final LoginUser loginUser;

    @GetMapping("/mypage")
    public String mypage(Model model, Authentication auth) {
        var user = loginUser.get(auth);
        model.addAttribute("user", user);
        return "mypage";
    }
}
