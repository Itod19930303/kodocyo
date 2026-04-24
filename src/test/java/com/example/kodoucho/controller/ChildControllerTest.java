package com.example.kodoucho.controller;

import com.example.kodoucho.entity.Child;
import com.example.kodoucho.entity.User;
import com.example.kodoucho.security.CustomUserDetailsService;
import com.example.kodoucho.security.LoginUser;
import com.example.kodoucho.security.OAuth2UserService;
import com.example.kodoucho.service.ChildService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ChildController の WebMvcTest
 * TDDサイクル: Red → Green → Refactor
 * 命名規則: メソッド名_条件_期待結果
 */
@WebMvcTest(ChildController.class)
class ChildControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean ChildService childService;
    @MockBean LoginUser loginUser;
    @MockBean CustomUserDetailsService customUserDetailsService;
    @MockBean OAuth2UserService oAuth2UserService;

    private User testParent;

    @BeforeEach
    void setUp() {
        testParent = User.builder()
                .id(1L)
                .name("テスト親")
                .email("parent@test.com")
                .role("PARENT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(loginUser.get(any(Authentication.class))).thenReturn(testParent);
    }

    // ── GET /children/new ────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "PARENT")
    void showNew_asParent_returnsChildForm() throws Exception {
        mockMvc.perform(get("/children/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("child/form"))
                .andExpect(model().attributeExists("childForm"));
    }

    // ── POST /children ────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "PARENT")
    void create_validForm_redirectsToFamily() throws Exception {
        when(childService.create(anyLong(), any())).thenReturn(
                Child.builder().id(2L).name("太郎").build()
        );

        mockMvc.perform(post("/children")
                        .param("name", "太郎")
                        .param("avatar", "🐣")
                        .param("themeColor", "#0060ad")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/family"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    void create_emptyName_returnsFormWithErrors() throws Exception {
        mockMvc.perform(post("/children")
                        .param("name", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("child/form"));

        verify(childService, never()).create(anyLong(), any());
    }

    // ── GET /children/{id}/edit ───────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "PARENT")
    void showEdit_validOwner_returnsChildForm() throws Exception {
        Child child = Child.builder()
                .id(1L).parentUserId(1L).name("花子")
                .avatar("🐱").themeColor("#ff0000")
                .build();
        when(childService.findByIdAndParent(1L, 1L)).thenReturn(child);

        mockMvc.perform(get("/children/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("child/form"))
                .andExpect(model().attributeExists("childForm", "child"));
    }

    // ── POST /children/{id}/delete ────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "PARENT")
    void delete_asParent_redirectsToFamily() throws Exception {
        mockMvc.perform(post("/children/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/family"));

        verify(childService).delete(1L, 1L);
    }

    // ── GET /family ────────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "PARENT")
    void family_asParent_returnsPageWithChildren() throws Exception {
        Child child = Child.builder().id(1L).parentUserId(1L).name("太郎").build();
        when(childService.findAllByParent(1L)).thenReturn(List.of(child));
        when(childService.calcBalance(1L)).thenReturn(5000);
        when(childService.getGradeLabel(any())).thenReturn("8歳・小学生");

        mockMvc.perform(get("/family"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("children", "balances", "gradeLabels"));
    }
}
