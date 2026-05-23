package com.example.kodoucho.security;

import com.example.kodoucho.entity.User;
import com.example.kodoucho.repository.UserRepository;
import com.example.kodoucho.service.EmailVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TwoFactorAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null && emailVerificationService.needsVerification(user)) {
            String sentTo = emailVerificationService.sendVerificationEmail(user);
            request.getSession().setAttribute("TWO_FA_PENDING", true);
            request.getSession().setAttribute("TWO_FA_EMAIL_SENT_TO", sentTo);
            response.sendRedirect("/auth/email-sent");
            return;
        }

        boolean isChild = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CHILD"));
        response.sendRedirect(isChild ? "/family" : "/dashboard");
    }
}
