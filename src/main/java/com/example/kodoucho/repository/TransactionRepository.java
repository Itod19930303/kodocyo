package com.example.kodoucho.repository;

import com.example.kodoucho.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByChildIdOrderByTransactionDateDesc(Long childId);

    @Query(value = "SELECT * FROM transactions WHERE child_id = :childId " +
           "AND EXTRACT(YEAR FROM transaction_date) = :year " +
           "AND EXTRACT(MONTH FROM transaction_date) = :month " +
           "ORDER BY transaction_date DESC",
           nativeQuery = true)
    List<Transaction> findByChildIdAndYearAndMonth(@Param("childId") Long childId,
                                                    @Param("year") int year,
                                                    @Param("month") int month);

    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE child_id = :childId AND type = 'income'",
           nativeQuery = true)
    Long sumIncomeByChildId(@Param("childId") Long childId);

    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE child_id = :childId AND type = 'expense'",
           nativeQuery = true)
    Long sumExpenseByChildId(@Param("childId") Long childId);

    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE child_id = :childId AND type = 'income' AND transaction_date <= :endDate",
           nativeQuery = true)
    Long sumIncomeByChildIdAndDateBefore(@Param("childId") Long childId, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE child_id = :childId AND type = 'expense' AND transaction_date <= :endDate",
           nativeQuery = true)
    Long sumExpenseByChildIdAndDateBefore(@Param("childId") Long childId, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE child_id IN :childIds AND type = 'income' AND EXTRACT(YEAR FROM transaction_date) = :year AND EXTRACT(MONTH FROM transaction_date) = :month",
           nativeQuery = true)
    Long sumMonthlyIncomeByChildIds(@Param("childIds") List<Long> childIds,
                                     @Param("year") int year,
                                     @Param("month") int month);

    List<Transaction> findTop5ByChildIdInOrderByTransactionDateDescCreatedAtDesc(List<Long> childIds);

    @Query(value = "SELECT DISTINCT CAST(EXTRACT(YEAR FROM transaction_date) * 100 + EXTRACT(MONTH FROM transaction_date) AS INTEGER) AS ym FROM transactions WHERE child_id = :childId ORDER BY ym DESC",
           nativeQuery = true)
    List<Integer> findDistinctYearMonthByChildId(@Param("childId") Long childId);
}
