package com.example.kodoucho.controller;

import com.example.kodoucho.dto.ChartDataDto;
import com.example.kodoucho.dto.TransactionForm;
import com.example.kodoucho.entity.*;
import com.example.kodoucho.security.LoginUser;
import com.example.kodoucho.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final ChildService childService;
    private final GoalService goalService;
    private final LoginUser loginUser;

    @GetMapping("/children/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(required = false) Integer yearMonth,
                         Model model, Authentication auth) {
        var user = loginUser.get(auth);
        Child child = childService.findById(id);

        // アクセス権チェック
        if ("CHILD".equals(user.getRole()) && !id.equals(child.getChildUserId())) {
            return "redirect:/family";
        }

        int balance = childService.calcBalance(id);
        List<Transaction> transactions;
        if (yearMonth != null) {
            int y = yearMonth / 100;
            int m = yearMonth % 100;
            transactions = transactionService.findByChildAndMonth(id, y, m);
        } else {
            transactions = transactionService.findByChild(id);
        }

        List<Goal> goals = goalService.findByChild(id);
        Map<Long, Integer> achievementRates = goals.stream()
                .collect(Collectors.toMap(Goal::getId, g -> goalService.getAchievementRate(g, balance)));
        Map<Long, Boolean> achievedMap = goals.stream()
                .collect(Collectors.toMap(Goal::getId, g -> goalService.isAchieved(g, balance)));

        boolean canEdit = "PARENT".equals(user.getRole()) || "PARTNER".equals(user.getRole());

        model.addAttribute("child", child);
        model.addAttribute("balance", balance);
        model.addAttribute("transactions", transactions);
        model.addAttribute("goals", goals);
        model.addAttribute("achievementRates", achievementRates);
        model.addAttribute("achievedMap", achievedMap);
        model.addAttribute("gradeLabel", childService.getGradeLabel(child.getBirthDate()));
        model.addAttribute("yearMonths", transactionService.findDistinctYearMonths(id));
        model.addAttribute("selectedYearMonth", yearMonth);
        model.addAttribute("user", user);
        model.addAttribute("canEdit", canEdit);
        return "transaction/detail";
    }

    @GetMapping("/children/{id}/transactions/new")
    public String showNew(@PathVariable Long id,
                          @RequestParam(defaultValue = "income") String type,
                          Model model, Authentication auth) {
        var user = loginUser.get(auth);
        Child child = childService.findById(id);
        TransactionForm form = new TransactionForm();
        form.setType(type);
        form.setTransactionDate(LocalDate.now());
        List<Goal> goals = goalService.findByChild(id);
        int balance = childService.calcBalance(id);
        model.addAttribute("transactionForm", form);
        model.addAttribute("child", child);
        model.addAttribute("goals", goals);
        model.addAttribute("balance", balance);
        model.addAttribute("user", user);
        return "transaction/form";
    }

    @PostMapping("/children/{id}/transactions")
    public String create(@PathVariable Long id,
                         @Valid @ModelAttribute TransactionForm transactionForm,
                         BindingResult result, Model model, Authentication auth) {
        var user = loginUser.get(auth);
        Child child = childService.findById(id);
        if (result.hasErrors()) {
            model.addAttribute("child", child);
            model.addAttribute("goals", goalService.findByChild(id));
            model.addAttribute("balance", childService.calcBalance(id));
            model.addAttribute("user", user);
            return "transaction/form";
        }
        transactionService.create(id, transactionForm);
        return "redirect:/children/" + id;
    }

    @PostMapping("/children/{childId}/transactions/{txId}/delete")
    public String delete(@PathVariable Long childId, @PathVariable Long txId) {
        transactionService.delete(txId);
        return "redirect:/children/" + childId;
    }

    @GetMapping("/children/{id}/chart-data")
    @ResponseBody
    public ResponseEntity<ChartDataDto> chartData(@PathVariable Long id,
                                                   @RequestParam(defaultValue = "6months") String period) {
        return ResponseEntity.ok(transactionService.getLineChartData(id, period));
    }
}
