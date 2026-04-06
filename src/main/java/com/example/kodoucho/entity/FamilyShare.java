package com.example.kodoucho.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_shares")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FamilyShare {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerUserId;

    private Long sharedUserId;

    @Column(nullable = false)
    private String invitedEmail;

    @Column(nullable = false, length = 20)
    private String shareRole; // PARTNER / GRANDPARENT

    @Column(nullable = false, length = 20)
    private String permission; // VIEW_ONLY / EDIT

    @Column(nullable = false, length = 20)
    private String status; // PENDING / ACCEPTED / REVOKED

    private String inviteToken;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = updatedAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
