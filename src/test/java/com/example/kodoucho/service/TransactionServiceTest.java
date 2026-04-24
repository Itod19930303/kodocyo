package com.example.kodoucho.service;

import com.example.kodoucho.dto.ChartDataDto;
import com.example.kodoucho.dto.TransactionForm;
import com.example.kodoucho.entity.Transaction;
import com.example.kodoucho.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TransactionService の Unit Test
 * TDDサイクル: Red → Green → Refactor
 * 命名規則: メソッド名_条件_期待結果
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void create_incomeType_categoryIsSetFromForm() {
        TransactionForm form = new TransactionForm();
        form.setType("income");
        form.setAmount(1000);
        form.setCategory("allowance");
        form.setMemo("お小遣い");
        form.setTransactionDate(LocalDate.now());

        transactionService.create(1L, form);

        verify(transactionRepository).save(argThat(tx ->
                "allowance".equals(tx.getCategory()) && tx.getAmount() == 1000
        ));
    }

    @Test
    void create_expenseType_categoryIsNull() {
        TransactionForm form = new TransactionForm();
        form.setType("expense");
        form.setAmount(500);
        form.setCategory("allowance");  // expenseでもcategoryをセットしても無視される
        form.setMemo("おもちゃ");
        form.setTransactionDate(LocalDate.now());

        transactionService.create(1L, form);

        verify(transactionRepository).save(argThat(tx ->
                tx.getCategory() == null && tx.getAmount() == 500
        ));
    }

    @Test
    void create_validForm_savesWithCorrectChildId() {
        TransactionForm form = new TransactionForm();
        form.setType("income");
        form.setAmount(3000);
        form.setCategory("new_year");
        form.setTransactionDate(LocalDate.of(2024, 1, 1));

        transactionService.create(42L, form);

        verify(transactionRepository).save(argThat(tx -> tx.getChildId().equals(42L)));
    }

    // ── getLineChartData ──────────────────────────────────────────────────────

    @Test
    void getLineChartData_6months_returns6Labels() {
        when(transactionRepository.sumIncomeByChildIdAndDateBefore(anyLong(), any(LocalDate.class)))
                .thenReturn(5000L);
        when(transactionRepository.sumExpenseByChildIdAndDateBefore(anyLong(), any(LocalDate.class)))
                .thenReturn(1000L);

        ChartDataDto dto = transactionService.getLineChartData(1L, "6months");

        assertThat(dto.getLabels()).hasSize(6);
        assertThat(dto.getData()).hasSize(6);
        assertThat(dto.getData()).allMatch(v -> v == 4000);
    }

    @Test
    void getLineChartData_1year_returns12Labels() {
        when(transactionRepository.sumIncomeByChildIdAndDateBefore(anyLong(), any(LocalDate.class)))
                .thenReturn(0L);
        when(transactionRepository.sumExpenseByChildIdAndDateBefore(anyLong(), any(LocalDate.class)))
                .thenReturn(0L);

        ChartDataDto dto = transactionService.getLineChartData(1L, "1year");

        assertThat(dto.getLabels()).hasSize(12);
        assertThat(dto.getData()).hasSize(12);
    }

    @Test
    void getLineChartData_balanceIsIncomeMinusExpense() {
        when(transactionRepository.sumIncomeByChildIdAndDateBefore(anyLong(), any(LocalDate.class)))
                .thenReturn(10000L);
        when(transactionRepository.sumExpenseByChildIdAndDateBefore(anyLong(), any(LocalDate.class)))
                .thenReturn(2500L);

        ChartDataDto dto = transactionService.getLineChartData(1L, "6months");

        assertThat(dto.getData()).allMatch(v -> v == 7500);
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_validId_callsRepositoryDeleteById() {
        transactionService.delete(99L);

        verify(transactionRepository).deleteById(99L);
    }
}
