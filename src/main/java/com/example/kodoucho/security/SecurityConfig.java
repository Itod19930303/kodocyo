package com.example.kodoucho.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final OAuth2UserService oAuth2UserService;
    private final TwoFactorAuthSuccessHandler twoFactorAuthSuccessHandler;
    private final TwoFactorAuthFilter twoFactorAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", "/register", "/css/**", "/js/**",
                    "/auth/verify", "/auth/email-sent",
                    "/family/sharing/accept/**"
                ).permitAll()
                .requestMatchers("/children/new", "/children", "/children/*/edit", "/children/*/delete").hasRole("PARENT")
                .requestMatchers("/family/sharing/**").hasRole("PARENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(twoFactorAuthSuccessHandler)
                .failureUrl("/login?error")
                .permitAll()
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                .userInfoEndpoint(ui -> ui.userService(oAuth2UserService))
                .successHandler(twoFactorAuthSuccessHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .addFilterAfter(twoFactorAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
