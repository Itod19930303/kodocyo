package com.example.kodoucho.controller;

import com.example.kodoucho.entity.EmailVerificationToken;
import com.example.kodoucho.entity.User;
import com.example.kodoucho.service.EmailVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @GetMapping("/email-sent")
    public String emailSent(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String sentTo = session != null ? (String) session.getAttribute("TWO_FA_EMAIL_SENT_TO") : null;
        model.addAttribute("sentTo", sentTo);
        return "auth/email-sent";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token, HttpServletRequest request) {
        Optional<EmailVerificationToken> tokenOpt = emailVerificationService.findValidToken(token);
        if (tokenOpt.isEmpty()) {
            return "auth/verify-error";
        }

        EmailVerificationToken verificationToken = tokenOpt.get();
        User user = verificationToken.getUser();
        emailVerificationService.consumeToken(verificationToken);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAnonymous = auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken;

        if (isAnonymous) {
            // Pattern 1: 会員登録後の初回認証 → 自動ログイン
            emailVerificationService.autoLogin(user, request);
        } else {
            // Pattern 2: 再認証完了 → 2FAフラグを解除
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("TWO_FA_PENDING");
                session.removeAttribute("TWO_FA_EMAIL_SENT_TO");
            }
        }

        return "CHILD".equals(user.getRole()) ? "redirect:/family" : "redirect:/dashboard";
    }
}
