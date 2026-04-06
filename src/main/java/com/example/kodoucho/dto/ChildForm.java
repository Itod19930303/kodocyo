package com.example.kodoucho.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class ChildForm {
    @NotBlank @Size(max = 20)
    private String name;

    private String avatar;

    private String themeColor;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
}
