package com.example.kodoucho.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class GoalForm {
    @NotBlank @Size(max = 30)
    private String name;

    @NotNull @Min(1)
    private Integer targetAmount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate targetDate;

    @Size(max = 20)
    private String purposeCategory;

    @Size(max = 200)
    private String message;

    @Size(max = 10)
    private String emoji;
}
