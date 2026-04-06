package com.example.kodoucho.service;

import com.example.kodoucho.dto.ChartDataDto;
import com.example.kodoucho.entity.Child;
import com.example.kodoucho.entity.Transaction;
import com.example.kodoucho.repository.ChildRepository;
import com.example.kodoucho.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ChildRepository childRepository;
    private final TransactionRepository transactionRepository;
    private final ChildService childService;

    public int getFamilyTotalBalance(Long parentUserId) {
        List<Child> children = childRepository.findByParentUserIdOrderByCreatedAtAsc(parentUserId);
        return children.stream().mapToInt(c -> childService.calcBalance(c.getId())).sum();
    }

    public double getMonthlyGrowthRate(Long parentUserId) {
        List<Child> children = childRepository.findByParentUserIdOrderByCreatedAtAsc(parentUserId);
        List<Long> childIds = children.stream().map(Child::getId).collect(Collectors.toList());
        if (childIds.isEmpty()) return 0.0;

        LocalDate now = LocalDate.now();
        long thisMonth = transactionRepository.sumMonthlyIncomeByChildIds(childIds, now.getYear(), now.getMonthValue());
        LocalDate lastMonth = now.minusMonths(1);
        long lastMonthIncome = transactionRepository.sumMonthlyIncomeByChildIds(childIds, lastMonth.getYear(), lastMonth.getMonthValue());

        if (lastMonthIncome == 0) return thisMonth > 0 ? 100.0 : 0.0;
        return (double)(thisMonth - lastMonthIncome) / lastMonthIncome * 100;
    }

    public ChartDataDto getMonthlyBarChartData(Long parentUserId) {
        List<Child> children = childRepository.findByParentUserIdOrderByCreatedAtAsc(parentUserId);
        List<Long> childIds = children.stream().map(Child::getId).collect(Collectors.toList());
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M月");
        for (int i = 5; i >= 0; i--) {
            LocalDate m = now.minusMonths(i);
            labels.add(m.format(fmt));
            if (childIds.isEmpty()) {
                data.add(0);
            } else {
                data.add(transactionRepository.sumMonthlyIncomeByChildIds(childIds, m.getYear(), m.getMonthValue()).intValue());
            }
        }
        return new ChartDataDto(labels, data);
    }

    public List<Transaction> getRecentTransactions(Long parentUserId) {
        List<Child> children = childRepository.findByParentUserIdOrderByCreatedAtAsc(parentUserId);
        List<Long> childIds = children.stream().map(Child::getId).collect(Collectors.toList());
        if (childIds.isEmpty()) return List.of();
        return transactionRepository.findTop5ByChildIdInOrderByTransactionDateDescCreatedAtDesc(childIds);
    }
}
