package com.example.kodoucho.service;

import com.example.kodoucho.dto.ChartDataDto;
import com.example.kodoucho.dto.TransactionForm;
import com.example.kodoucho.entity.Transaction;
import com.example.kodoucho.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> findByChild(Long childId) {
        return transactionRepository.findByChildIdOrderByTransactionDateDesc(childId);
    }

    public List<Transaction> findByChildAndMonth(Long childId, int year, int month) {
        return transactionRepository.findByChildIdAndYearAndMonth(childId, year, month);
    }

    public List<Integer> findDistinctYearMonths(Long childId) {
        return transactionRepository.findDistinctYearMonthByChildId(childId);
    }

    @Transactional
    public void create(Long childId, TransactionForm form) {
        Transaction tx = Transaction.builder()
                .childId(childId)
                .type(form.getType())
                .amount(form.getAmount())
                .category("income".equals(form.getType()) ? form.getCategory() : null)
                .memo(form.getMemo())
                .transactionDate(form.getTransactionDate())
                .build();
        transactionRepository.save(tx);
    }

    @Transactional
    public void delete(Long txId, Long childId) {
        Transaction tx = transactionRepository.findById(txId)
                .orElseThrow(() -> new IllegalArgumentException("取引が見つかりません"));
        if (!tx.getChildId().equals(childId)) {
            throw new IllegalArgumentException("アクセス権限がありません");
        }
        transactionRepository.deleteById(txId);
    }

    public ChartDataDto getLineChartData(Long childId, String period) {
        int months = "1year".equals(period) ? 12 : 6;
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M月");
        for (int i = months - 1; i >= 0; i--) {
            LocalDate endOfMonth = now.minusMonths(i).withDayOfMonth(
                    now.minusMonths(i).lengthOfMonth());
            long income = transactionRepository.sumIncomeByChildIdAndDateBefore(childId, endOfMonth);
            long expense = transactionRepository.sumExpenseByChildIdAndDateBefore(childId, endOfMonth);
            labels.add(endOfMonth.format(fmt));
            data.add((int)(income - expense));
        }
        return new ChartDataDto(labels, data);
    }
}
