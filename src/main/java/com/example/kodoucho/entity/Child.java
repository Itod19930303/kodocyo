package com.example.kodoucho.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "children")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Child {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long parentUserId;

    private Long childUserId;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private String avatar;

    @Column(nullable = false, length = 7)
    private String themeColor;

    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = updatedAt = LocalDateTime.now();
        if (avatar == null || avatar.isBlank()) avatar = "🐣";
        if (themeColor == null || themeColor.isBlank()) themeColor = "#0060ad";
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
