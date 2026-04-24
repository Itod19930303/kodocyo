package com.example.kodoucho.security;

import com.example.kodoucho.controller.ChildController;
import com.example.kodoucho.entity.User;
import com.example.kodoucho.service.ChildService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * URLレベルのアクセス制御テスト
 * SecurityConfig.java に定義されたロール制限を検証する
 *
 * テスト対象ルール:
 *   /children/new, /children, /children/{id}/edit, /children/{id}/delete → PARENT のみ
 *   それ以外の認証済みエンドポイント → 全ロールOK
 *   未認証 → OAuth2認証ページへリダイレクト
 */
@WebMvcTest(ChildController.class)
@Import(SecurityConfig.class)
class SecurityAccessTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean ChildService childService;
    @MockBean LoginUser loginUser;
    @MockBean CustomUserDetailsService customUserDetailsService;
    @MockBean OAuth2UserService oAuth2UserService;

    @BeforeEach
    void setUp() {
        User testParent = User.builder()
                .id(1L)
                .name("テスト親")
                .email("parent@test.com")
                .role("PARENT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(loginUser.get(any(Authentication.class))).thenReturn(testParent);
    }

    // ── 未認証アクセス ──────────────────────────────────────────────────────────

    @Test
    void childrenNew_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/children/new"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void childrenPost_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/children").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void family_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/family"))
                .andExpect(status().is3xxRedirection());
    }

    // ── PARENT のみアクセス可能 ─────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "PARENT")
    void childrenNew_asParent_returns200() throws Exception {
        mockMvc.perform(get("/children/new"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void childrenNew_asPartner_returns403() throws Exception {
        mockMvc.perform(get("/children/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "GRANDPARENT")
    void childrenNew_asGrandparent_returns403() throws Exception {
        mockMvc.perform(get("/children/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CHILD")
    void childrenNew_asChild_returns403() throws Exception {
        mockMvc.perform(get("/children/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PARENT")
    void childrenDelete_asParent_redirects() throws Exception {
        mockMvc.perform(post("/children/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void childrenDelete_asPartner_returns403() throws Exception {
        mockMvc.perform(post("/children/1/delete").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
