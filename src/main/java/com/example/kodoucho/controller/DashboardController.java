package com.example.kodoucho.controller;

import com.example.kodoucho.dto.ChartDataDto;
import com.example.kodoucho.entity.*;
import com.example.kodoucho.security.LoginUser;
import com.example.kodoucho.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ChildService childService;
    private final GoalService goalService;
    private final LoginUser loginUser;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        var user = loginUser.get(auth);
        List<Child> children = childService.findAllByParent(user.getId());
        Map<Long, Integer> balances = children.stream()
                .collect(Collectors.toMap(Child::getId, c -> childService.calcBalance(c.getId())));
        Map<Long, String> gradeLabels = children.stream()
                .collect(Collectors.toMap(Child::getId, c -> childService.getGradeLabel(c.getBirthDate())));
        Map<Long, List<Goal>> goalsMap = children.stream()
                .collect(Collectors.toMap(Child::getId, c -> goalService.findByChild(c.getId())));
        Map<Long, Integer> firstGoalRates = new HashMap<>();
        for (Child child : children) {
            List<Goal> goals = goalsMap.get(child.getId());
            if (!goals.isEmpty()) {
                int rate = goalService.getAchievementRate(goals.get(0), balances.get(child.getId()));
                firstGoalRates.put(child.getId(), rate);
            }
        }

        int familyTotal = dashboardService.getFamilyTotalBalance(user.getId());
        double growthRate = dashboardService.getMonthlyGrowthRate(user.getId());
        ChartDataDto barChart = dashboardService.getMonthlyBarChartData(user.getId());
        List<Transaction> recentTxs = dashboardService.getRecentTransactions(user.getId());

        Map<Long, String> childNames = children.stream()
                .collect(Collectors.toMap(Child::getId, Child::getName));

        model.addAttribute("user", user);
        model.addAttribute("children", children);
        model.addAttribute("balances", balances);
        model.addAttribute("gradeLabels", gradeLabels);
        model.addAttribute("goalsMap", goalsMap);
        model.addAttribute("firstGoalRates", firstGoalRates);
        model.addAttribute("familyTotal", familyTotal);
        model.addAttribute("growthRate", growthRate);
        model.addAttribute("barChartLabels", barChart.getLabels());
        model.addAttribute("barChartData", barChart.getData());
        model.addAttribute("recentTxs", recentTxs);
        model.addAttribute("childNames", childNames);
        return "dashboard";
    }
}
