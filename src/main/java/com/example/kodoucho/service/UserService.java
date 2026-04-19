package com.example.kodoucho.service;

import com.example.kodoucho.dto.RegisterForm;
import com.example.kodoucho.entity.User;
import com.example.kodoucho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("このメールアドレスはすでに登録されています");
        }
        User user = User.builder()
                .name(form.getName())
                .email(form.getEmail())
                .passwordHash(passwordEncoder.encode(form.getPassword()))
                .role("PARENT")
                .build();
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }
}
