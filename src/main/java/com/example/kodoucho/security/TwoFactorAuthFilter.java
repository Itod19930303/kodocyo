package com.example.kodoucho.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class TwoFactorAuthFilter extends OncePerRequestFilter {

    private static final Set<String> ALLOWED_PATHS = Set.of(
            "/auth/verify", "/auth/email-sent",
            "/login", "/register", "/logout", "/error"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getServletPath();

        if (isAllowed(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null && Boolean.TRUE.equals(session.getAttribute("TWO_FA_PENDING"))) {
            response.sendRedirect("/auth/email-sent");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isAllowed(String path) {
        return ALLOWED_PATHS.contains(path)
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/oauth2/")
                || path.startsWith("/family/sharing/accept/");
    }
}
