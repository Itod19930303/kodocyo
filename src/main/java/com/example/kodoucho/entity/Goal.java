package com.example.kodoucho.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Goal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long childId;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false)
    private Integer targetAmount;

    private LocalDate targetDate;

    @Column(length = 20)
    private String purposeCategory;

    @Column(length = 200)
    private String message;

    @Column(nullable = false, length = 10)
    private String emoji;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        if (emoji == null || emoji.isBlank()) emoji = "🎯";
    }
}
