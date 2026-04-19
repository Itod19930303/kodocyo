package com.example.kodoucho.controller;

import com.example.kodoucho.dto.ChartDataDto;
import com.example.kodoucho.entity.Child;
import com.example.kodoucho.entity.User;
import com.example.kodoucho.security.CustomUserDetailsService;
import com.example.kodoucho.security.LoginUser;
import com.example.kodoucho.security.OAuth2UserService;
import com.example.kodoucho.service.ChildService;
import com.example.kodoucho.service.GoalService;
import com.example.kodoucho.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TransactionService transactionService;

    @MockBean
    ChildService childService;

    @MockBean
    GoalService goalService;

    @MockBean
    LoginUser loginUser;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @MockBean
    OAuth2UserService oAuth2UserService;

    private User parentUser;
    private Child child;

    @BeforeEach
    void setUp() {
        parentUser = User.builder()
                .id(1L)
                .name("テスト")
                .email("test@example.com")
                .role("PARENT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        child = Child.builder()
                .id(1L)
                .parentUserId(1L)
                .name("テスト子供")
                .avatar("🐣")
                .themeColor("#0060ad")
                .birthDate(LocalDate.of(2015, 4, 1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 1. PARENT で GET /children/1 → 200
    @Test
    @WithMockUser(roles = "PARENT")
    void detail_asParent_withOwnChild_returns200() throws Exception {
        when(loginUser.get(any())).thenReturn(parentUser);
        when(childService.findByIdAndParent(1L, 1L)).thenReturn(child);
        when(childService.calcBalance(1L)).thenReturn(5000);
        when(transactionService.findByChild(1L)).thenReturn(Collections.emptyList());
        when(goalService.findByChild(1L)).thenReturn(Collections.emptyList());
        when(childService.getGradeLabel(any())).thenReturn("小1");
        when(transactionService.findDistinctYearMonths(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/children/1"))
                .andExpect(status().isOk());
    }

    // 2. childService.findByIdAndParent が IllegalArgumentException → 500
    @Test
    @WithMockUser(roles = "PARENT")
    void detail_asParent_withOtherChild_returns5xx() throws Exception {
        when(loginUser.get(any())).thenReturn(parentUser);
        when(childService.findByIdAndParent(1L, 1L))
                .thenThrow(new IllegalArgumentException("Child not found"));

        mockMvc.perform(get("/children/1"))
                .andExpect(status().is5xxServerError());
    }

    // 3. 未認証 GET /children/1/transactions/new → 302
    @Test
    void showNew_notAuthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/children/1/transactions/new"))
                .andExpect(status().is3xxRedirection());
    }

    // 4. PARENT POST /children/1/transactions/2/delete with csrf → 302 + verify delete呼ばれた
    @Test
    @WithMockUser(roles = "PARENT")
    void delete_asParent_callsServiceDelete() throws Exception {
        when(loginUser.get(any())).thenReturn(parentUser);
        when(childService.findByIdAndParent(1L, 1L)).thenReturn(child);
        doNothing().when(transactionService).delete(2L, 1L);

        mockMvc.perform(post("/children/1/transactions/2/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/children/1"));

        verify(transactionService).delete(2L, 1L);
    }

    // 5. PARENT GET /children/1/chart-data → 200 + JSON
    @Test
    @WithMockUser(roles = "PARENT")
    void chartData_asParent_returns200WithJson() throws Exception {
        when(loginUser.get(any())).thenReturn(parentUser);
        when(childService.findByIdAndParent(1L, 1L)).thenReturn(child);
        ChartDataDto chartData = new ChartDataDto(
                List.of("2024-01", "2024-02", "2024-03"),
                List.of(1000, 2000, 3000)
        );
        when(transactionService.getLineChartData(1L, "6months")).thenReturn(chartData);

        mockMvc.perform(get("/children/1/chart-data"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }
}
