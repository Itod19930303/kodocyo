package com.example.kodoucho.repository;

import com.example.kodoucho.entity.EmailVerificationToken;
import com.example.kodoucho.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByTokenAndUsedFalseAndExpiresAtAfter(String token, LocalDateTime now);

    void deleteByUser(User user);
}
