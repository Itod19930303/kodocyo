package com.example.kodoucho.service;

import com.example.kodoucho.entity.EmailVerificationToken;
import com.example.kodoucho.entity.User;
import com.example.kodoucho.repository.EmailVerificationTokenRepository;
import com.example.kodoucho.repository.UserRepository;
import com.example.kodoucho.security.CustomUserDetailsService;
import com.example.kodoucho.security.EmailVerificationProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final EmailVerificationProperties properties;
    private final CustomUserDetailsService userDetailsService;

    /**
     * 認証メールを送信し、送付先メールアドレスを返す。
     * override-to が設定されている場合はそちらへ送付（テスト用）。
     */
    @Transactional
    public String sendVerificationEmail(User user) {
        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.save(EmailVerificationToken.builder()
                .user(user)
                .token(token)
                .createdAt(now)
                .expiresAt(now.plusHours(properties.getTokenExpiryHours()))
                .used(false)
                .build());

        String to = StringUtils.hasText(properties.getOverrideTo())
                ? properties.getOverrideTo()
                : user.getEmail();

        String verifyUrl = properties.getBaseUrl() + "/auth/verify?token=" + token;
        log.info("[EmailVerification] 認証URL: {}", verifyUrl);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("【こどちょ】メールアドレス確認");
            message.setText(
                    user.getName() + " さん\n\n"
                    + "以下のURLをクリックしてメールアドレスを確認してください。\n\n"
                    + verifyUrl + "\n\n"
                    + "このURLは" + properties.getTokenExpiryHours() + "時間有効です。\n"
                    + "心当たりのない場合はこのメールを無視してください。\n\n"
                    + "こどちょ"
            );
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("[EmailVerification] メール送信失敗 (SMTPが未設定の場合は上記のURLを直接利用してください): {}", e.getMessage());
        }

        return to;
    }

    public Optional<EmailVerificationToken> findValidToken(String token) {
        return tokenRepository.findByTokenAndUsedFalseAndExpiresAtAfter(token, LocalDateTime.now());
    }

    @Transactional
    public void consumeToken(EmailVerificationToken verificationToken) {
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        User user = verificationToken.getUser();
        user.setLastVerifiedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public boolean needsVerification(User user) {
        if (user.getLastVerifiedAt() == null) return true;
        return user.getLastVerifiedAt()
                .isBefore(LocalDateTime.now().minusDays(properties.getRequiredDays()));
    }

    /** Pattern 1（会員登録後）のメール認証完了時に自動ログインする */
    public void autoLogin(User user, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
    }
}
