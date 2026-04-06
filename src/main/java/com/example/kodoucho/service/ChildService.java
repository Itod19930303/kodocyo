package com.example.kodoucho.service;

import com.example.kodoucho.dto.ChildForm;
import com.example.kodoucho.entity.Child;
import com.example.kodoucho.repository.ChildRepository;
import com.example.kodoucho.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final TransactionRepository transactionRepository;

    public List<Child> findAllByParent(Long parentUserId) {
        return childRepository.findByParentUserIdOrderByCreatedAtAsc(parentUserId);
    }

    public Child findById(Long id) {
        return childRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("子供が見つかりません"));
    }

    public Child findByIdAndParent(Long id, Long parentUserId) {
        return childRepository.findByIdAndParentUserId(id, parentUserId)
                .orElseThrow(() -> new IllegalArgumentException("アクセス権限がありません"));
    }

    @Transactional
    public Child create(Long parentUserId, ChildForm form) {
        Child child = Child.builder()
                .parentUserId(parentUserId)
                .name(form.getName())
                .avatar(form.getAvatar() != null && !form.getAvatar().isBlank() ? form.getAvatar() : "🐣")
                .themeColor(form.getThemeColor() != null && !form.getThemeColor().isBlank() ? form.getThemeColor() : "#0060ad")
                .birthDate(form.getBirthDate())
                .build();
        return childRepository.save(child);
    }

    @Transactional
    public void update(Long id, Long parentUserId, ChildForm form) {
        Child child = findByIdAndParent(id, parentUserId);
        child.setName(form.getName());
        if (form.getAvatar() != null && !form.getAvatar().isBlank()) child.setAvatar(form.getAvatar());
        if (form.getThemeColor() != null && !form.getThemeColor().isBlank()) child.setThemeColor(form.getThemeColor());
        child.setBirthDate(form.getBirthDate());
        childRepository.save(child);
    }

    @Transactional
    public void delete(Long id, Long parentUserId) {
        Child child = findByIdAndParent(id, parentUserId);
        childRepository.delete(child);
    }

    public int calcBalance(Long childId) {
        long income = transactionRepository.sumIncomeByChildId(childId);
        long expense = transactionRepository.sumExpenseByChildId(childId);
        return (int)(income - expense);
    }

    public String getGradeLabel(LocalDate birthDate) {
        if (birthDate == null) return "";
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        String grade;
        if (age <= 2) grade = "乳幼児";
        else if (age <= 5) grade = "幼稚園・保育園";
        else if (age <= 11) grade = "小学生";
        else if (age <= 14) grade = "中学生";
        else if (age <= 17) grade = "高校生";
        else grade = "";
        return grade.isEmpty() ? age + "歳" : age + "歳・" + grade;
    }
}
