package com.example.kodoucho.security;

import com.example.kodoucho.entity.User;
import com.example.kodoucho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginUser {

    private final UserRepository userRepository;

    public User get(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User ud) {
            return userRepository.findByEmail(ud.getUsername())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        }
        if (principal instanceof OAuth2User oauth) {
            String email = oauth.getAttribute("email");
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        }
        throw new IllegalStateException("Unknown principal type");
    }
}
