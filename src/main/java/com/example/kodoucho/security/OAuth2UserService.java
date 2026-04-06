package com.example.kodoucho.security;

import com.example.kodoucho.entity.User;
import com.example.kodoucho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OAuth2UserService implements org.springframework.security.oauth2.client.userinfo.OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(request);
        String provider = request.getClientRegistration().getRegistrationId();
        String subject = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByOauthProviderAndOauthSubject(provider, subject)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .orElseGet(() -> userRepository.save(User.builder()
                                .name(name != null ? name.substring(0, Math.min(name.length(), 30)) : email)
                                .email(email)
                                .oauthProvider(provider)
                                .oauthSubject(subject)
                                .role("PARENT")
                                .build())));

        if (user.getOauthSubject() == null) {
            user.setOauthProvider(provider);
            user.setOauthSubject(subject);
            userRepository.save(user);
        }

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("userId", user.getId());
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                attributes,
                "email"
        );
    }
}
