package com.example.kodoucho.service;

import com.example.kodoucho.entity.Goal;
import com.example.kodoucho.repository.GoalRepository;
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
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private Goal goal;

    @BeforeEach
    void setUp() {
        goal = Goal.builder()
                .id(1L)
                .childId(1L)
                .name("新しいゲーム機")
                .targetAmount(30000)
                .targetDate(LocalDate.of(2024, 12, 25))
                .purposeCategory("おもちゃ")
                .message("頑張ってためよう")
                .emoji("🎮")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // findByIdAndChild: childId 一致で Goal が返る
    @Test
    void findByIdAndChild_childMatches_returnsGoal() {
        when(goalRepository.findByIdAndChildId(1L, 1L)).thenReturn(Optional.of(goal));

        Goal result = goalService.findByIdAndChild(1L, 1L);

        assertThat(result).isEqualTo(goal);
        verify(goalRepository).findByIdAndChildId(1L, 1L);
    }

    // findByIdAndChild: childId 不一致で IllegalArgumentException
    @Test
    void findByIdAndChild_childMismatch_throwsIllegalArgumentException() {
        when(goalRepository.findByIdAndChildId(1L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.findByIdAndChild(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("目標が見つかりません");

        verify(goalRepository).findByIdAndChildId(1L, 99L);
    }

    // save: repository.save が呼ばれ同じオブジェクトが返る
    @Test
    void save_goal_delegatesToRepository() {
        when(goalRepository.save(goal)).thenReturn(goal);

        Goal result = goalRepository.save(goal);

        assertThat(result).isEqualTo(goal);
        verify(goalRepository).save(goal);
    }

    // delete: childId 一致のとき repository.delete が呼ばれる
    @Test
    void delete_ownedGoal_callsRepositoryDelete() {
        when(goalRepository.findByIdAndChildId(1L, 1L)).thenReturn(Optional.of(goal));

        goalService.delete(1L, 1L);

        verify(goalRepository).delete(goal);
    }

    // delete: childId 不一致のとき IllegalArgumentException（deleteは呼ばれない）
    @Test
    void delete_notOwnedGoal_throwsIllegalArgumentException() {
        when(goalRepository.findByIdAndChildId(1L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.delete(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("目標が見つかりません");

        verify(goalRepository, never()).delete(any(Goal.class));
    }
}
