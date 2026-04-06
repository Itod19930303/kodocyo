package com.example.kodoucho.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class TransactionForm {
    @NotNull(message = "金額を入力してください")
    @Min(value = 1, message = "1円以上の金額を入力してください")
    private Integer amount;

    private String category;

    @NotNull(message = "日付を入力してください")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate transactionDate;

    @Size(max = 100)
    private String memo;

    @NotBlank
    private String type; // income / expense
}
