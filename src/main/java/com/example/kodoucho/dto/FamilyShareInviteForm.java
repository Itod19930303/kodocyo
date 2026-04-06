package com.example.kodoucho.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FamilyShareInviteForm {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String shareRole; // PARTNER / GRANDPARENT

    @NotBlank
    private String permission; // VIEW_ONLY / EDIT
}
