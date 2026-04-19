package com.example.kodoucho.service;

import com.example.kodoucho.entity.Transaction;
import com.example.kodoucho.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction incomeTransaction;
    private Transaction expenseTransaction;

    @BeforeEach
    void setUp() {
        incomeTransaction = Transaction.builder()
                .id(1L)
                .childId(1L)
                .type("income")
                .amount(3000)
                .memo("お小遣い")
                .transactionDate(LocalDate.of(2024, 1, 10))
                .createdAt(LocalDateTime.now())
                .build();

        expenseTransaction = Transaction.builder()
                .id(2L)
                .childId(1L)
                .type("expense")
                .amount(500)
                .memo("おやつ")
                .transactionDate(LocalDate.of(2024, 1, 15))
                .createdAt(LocalDateTime.now())
                .build();
    }

    // save: repository.save が呼ばれ同じオブジェクトが返る
    @Test
    void save_transaction_delegatesToRepository() {
        when(transactionRepository.save(incomeTransaction)).thenReturn(incomeTransaction);

        Transaction result = transactionRepository.save(incomeTransaction);

        assertThat(result).isEqualTo(incomeTransaction);
        verify(transactionRepository).save(incomeTransaction);
    }

    // delete: childId 一致のとき deleteById が呼ばれる
    @Test
    void delete_ownedTransaction_callsDeleteById() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(incomeTransaction));

        transactionService.delete(1L, 1L);

        verify(transactionRepository).deleteById(1L);
    }

    // delete: childId 不一致のとき IllegalArgumentException
    @Test
    void delete_notOwnedTransaction_throwsIllegalArgumentException() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(incomeTransaction));

        assertThatThrownBy(() -> transactionService.delete(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("アクセス権限がありません");

        verify(transactionRepository, never()).deleteById(any());
    }

    // delete: 存在しない txId のとき IllegalArgumentException
    @Test
    void delete_notFoundTransaction_throwsIllegalArgumentException() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.delete(99L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("取引が見つかりません");
    }

}
