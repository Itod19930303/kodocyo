package com.example.kodoucho.service;

import com.example.kodoucho.dto.GoalForm;
import com.example.kodoucho.entity.Goal;
import com.example.kodoucho.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public List<Goal> findByChild(Long childId) {
        return goalRepository.findByChildIdOrderByCreatedAtAsc(childId);
    }

    public Goal findById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("目標が見つかりません"));
    }

    public Goal findByIdAndChild(Long goalId, Long childId) {
        return goalRepository.findByIdAndChildId(goalId, childId)
                .orElseThrow(() -> new IllegalArgumentException("目標が見つかりません"));
    }

    @Transactional
    public void create(Long childId, GoalForm form) {
        Goal goal = Goal.builder()
                .childId(childId)
                .name(form.getName())
                .targetAmount(form.getTargetAmount())
                .targetDate(form.getTargetDate())
                .purposeCategory(form.getPurposeCategory())
                .message(form.getMessage())
                .emoji(form.getEmoji() != null && !form.getEmoji().isBlank() ? form.getEmoji() : "🎯")
                .build();
        goalRepository.save(goal);
    }

    @Transactional
    public void update(Long goalId, Long childId, GoalForm form) {
        Goal goal = findByIdAndChild(goalId, childId);
        goal.setName(form.getName());
        goal.setTargetAmount(form.getTargetAmount());
        goal.setTargetDate(form.getTargetDate());
        goal.setPurposeCategory(form.getPurposeCategory());
        goal.setMessage(form.getMessage());
        if (form.getEmoji() != null && !form.getEmoji().isBlank()) goal.setEmoji(form.getEmoji());
        goalRepository.save(goal);
    }

    @Transactional
    public void delete(Long goalId, Long childId) {
        Goal goal = findByIdAndChild(goalId, childId);
        goalRepository.delete(goal);
    }

    public boolean isAchieved(Goal goal, int balance) {
        return balance >= goal.getTargetAmount();
    }

    public int getAchievementRate(Goal goal, int balance) {
        return Math.min(100, (int) Math.floor((double) balance / goal.getTargetAmount() * 100));
    }
}
