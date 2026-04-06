package com.example.kodoucho.repository;

import com.example.kodoucho.entity.FamilyShare;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FamilyShareRepository extends JpaRepository<FamilyShare, Long> {
    List<FamilyShare> findByOwnerUserIdOrderByCreatedAtDesc(Long ownerUserId);
    Optional<FamilyShare> findByInviteToken(String token);
    Optional<FamilyShare> findByIdAndOwnerUserId(Long id, Long ownerUserId);
    List<FamilyShare> findBySharedUserId(Long sharedUserId);
}
