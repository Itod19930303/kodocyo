package com.example.kodoucho.service;

import com.example.kodoucho.entity.Child;
import com.example.kodoucho.repository.ChildRepository;
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
class ChildServiceTest {

    @Mock
    private ChildRepository childRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ChildService childService;

    private Child child;

    @BeforeEach
    void setUp() {
        child = Child.builder()
                .id(1L)
                .parentUserId(10L)
                .name("テスト子供")
                .avatar("🐣")
                .themeColor("#0060ad")
                .birthDate(LocalDate.of(2015, 4, 1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // findByIdAndParent: 所有者一致で Child が返る
    @Test
    void findByIdAndParent_ownerMatches_returnsChild() {
        when(childRepository.findByIdAndParentUserId(1L, 10L)).thenReturn(Optional.of(child));

        Child result = childService.findByIdAndParent(1L, 10L);

        assertThat(result).isEqualTo(child);
        verify(childRepository).findByIdAndParentUserId(1L, 10L);
    }

    // findByIdAndParent: 所有者不一致で IllegalArgumentException
    @Test
    void findByIdAndParent_ownerMismatch_throwsIllegalArgumentException() {
        when(childRepository.findByIdAndParentUserId(1L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> childService.findByIdAndParent(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("アクセス権限がありません");

        verify(childRepository).findByIdAndParentUserId(1L, 99L);
    }

    // save (create経由でなく repository.save 直呼び): 正常保存
    @Test
    void save_child_delegatesToRepository() {
        when(childRepository.save(child)).thenReturn(child);

        Child result = childRepository.save(child);

        assertThat(result).isEqualTo(child);
        verify(childRepository).save(child);
    }

    // delete (delete(id, parentUserId) 経由): 正常削除
    @Test
    void delete_ownedChild_callsRepositoryDelete() {
        when(childRepository.findByIdAndParentUserId(1L, 10L)).thenReturn(Optional.of(child));

        childService.delete(1L, 10L);

        verify(childRepository).delete(child);
    }

    // calcBalance: income合計 - expense合計 が正しい
    @Test
    void calcBalance_returnsIncomeMinusExpense() {
        when(transactionRepository.sumIncomeByChildId(1L)).thenReturn(5000L);
        when(transactionRepository.sumExpenseByChildId(1L)).thenReturn(1500L);

        int balance = childService.calcBalance(1L);

        assertThat(balance).isEqualTo(3500);
    }
}
