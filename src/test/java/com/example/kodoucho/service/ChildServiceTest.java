package com.example.kodoucho.service;

import com.example.kodoucho.dto.ChildForm;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ChildService の Unit Test
 * TDDサイクル: Red → Green → Refactor
 * 命名規則: メソッド名_条件_期待結果
 */
@ExtendWith(MockitoExtension.class)
class ChildServiceTest {

    @Mock
    private ChildRepository childRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ChildService childService;

    private Child sampleChild;

    @BeforeEach
    void setUp() {
        sampleChild = Child.builder()
                .id(1L)
                .parentUserId(10L)
                .name("太郎")
                .avatar("🐣")
                .themeColor("#0060ad")
                .birthDate(LocalDate.of(2018, 4, 1))
                .build();
    }

    // ── calcBalance ──────────────────────────────────────────────────────────

    @Test
    void calcBalance_noTransactions_returnsZero() {
        when(transactionRepository.sumIncomeByChildId(1L)).thenReturn(0L);
        when(transactionRepository.sumExpenseByChildId(1L)).thenReturn(0L);

        int balance = childService.calcBalance(1L);

        assertThat(balance).isZero();
    }

    @Test
    void calcBalance_incomeOnly_returnsIncomeAmount() {
        when(transactionRepository.sumIncomeByChildId(1L)).thenReturn(5000L);
        when(transactionRepository.sumExpenseByChildId(1L)).thenReturn(0L);

        assertThat(childService.calcBalance(1L)).isEqualTo(5000);
    }

    @Test
    void calcBalance_incomeAndExpense_returnsCorrectDifference() {
        when(transactionRepository.sumIncomeByChildId(1L)).thenReturn(10000L);
        when(transactionRepository.sumExpenseByChildId(1L)).thenReturn(3000L);

        assertThat(childService.calcBalance(1L)).isEqualTo(7000);
    }

    // ── getGradeLabel ─────────────────────────────────────────────────────────

    @Test
    void getGradeLabel_nullBirthDate_returnsEmpty() {
        assertThat(childService.getGradeLabel(null)).isEmpty();
    }

    @Test
    void getGradeLabel_age1_returns乳幼児() {
        LocalDate birthDate = LocalDate.now().minusYears(1);
        assertThat(childService.getGradeLabel(birthDate)).contains("乳幼児");
    }

    @Test
    void getGradeLabel_age4_returns幼稚園() {
        LocalDate birthDate = LocalDate.now().minusYears(4);
        assertThat(childService.getGradeLabel(birthDate)).contains("幼稚園");
    }

    @Test
    void getGradeLabel_age8_returns小学生() {
        LocalDate birthDate = LocalDate.now().minusYears(8);
        assertThat(childService.getGradeLabel(birthDate)).contains("小学生");
    }

    @Test
    void getGradeLabel_age13_returns中学生() {
        LocalDate birthDate = LocalDate.now().minusYears(13);
        assertThat(childService.getGradeLabel(birthDate)).contains("中学生");
    }

    @Test
    void getGradeLabel_age16_returns高校生() {
        LocalDate birthDate = LocalDate.now().minusYears(16);
        assertThat(childService.getGradeLabel(birthDate)).contains("高校生");
    }

    @Test
    void getGradeLabel_age20_returnsAgeOnly() {
        LocalDate birthDate = LocalDate.now().minusYears(20);
        String label = childService.getGradeLabel(birthDate);
        assertThat(label).startsWith("20歳");
        assertThat(label).doesNotContain("・");
    }

    // ── findByIdAndParent ─────────────────────────────────────────────────────

    @Test
    void findByIdAndParent_validOwner_returnsChild() {
        when(childRepository.findByIdAndParentUserId(1L, 10L)).thenReturn(Optional.of(sampleChild));

        Child result = childService.findByIdAndParent(1L, 10L);

        assertThat(result.getName()).isEqualTo("太郎");
    }

    @Test
    void findByIdAndParent_wrongParent_throwsIllegalArgumentException() {
        when(childRepository.findByIdAndParentUserId(1L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> childService.findByIdAndParent(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("アクセス権限");
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void create_validForm_savesChildWithDefaultAvatar() {
        ChildForm form = new ChildForm();
        form.setName("花子");
        form.setAvatar("");
        form.setThemeColor("");
        form.setBirthDate(LocalDate.of(2020, 1, 1));

        when(childRepository.save(any(Child.class))).thenAnswer(inv -> inv.getArgument(0));

        Child saved = childService.create(10L, form);

        assertThat(saved.getName()).isEqualTo("花子");
        assertThat(saved.getAvatar()).isEqualTo("🐣");
        assertThat(saved.getThemeColor()).isEqualTo("#0060ad");
        verify(childRepository).save(any(Child.class));
    }

    @Test
    void create_validFormWithCustomAvatar_savesChildWithGivenAvatar() {
        ChildForm form = new ChildForm();
        form.setName("次郎");
        form.setAvatar("🐱");
        form.setThemeColor("#ff0000");
        form.setBirthDate(null);

        when(childRepository.save(any(Child.class))).thenAnswer(inv -> inv.getArgument(0));

        Child saved = childService.create(10L, form);

        assertThat(saved.getAvatar()).isEqualTo("🐱");
        assertThat(saved.getThemeColor()).isEqualTo("#ff0000");
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_validOwner_deletesChild() {
        when(childRepository.findByIdAndParentUserId(1L, 10L)).thenReturn(Optional.of(sampleChild));

        childService.delete(1L, 10L);

        verify(childRepository).delete(sampleChild);
    }
}
