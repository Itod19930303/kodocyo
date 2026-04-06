package com.example.kodoucho.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterForm {
    @NotBlank
    private String name;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8, message = "パスワードは8文字以上で入力してください")
    private String password;

    @AssertTrue(message = "利用規約に同意してください")
    private boolean agreeTerms;
}
