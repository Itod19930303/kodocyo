package com.example.kodoucho.service;

import com.example.kodoucho.dto.FamilyShareInviteForm;
import com.example.kodoucho.entity.FamilyShare;
import com.example.kodoucho.repository.FamilyShareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilySharingService {

    private final FamilyShareRepository familyShareRepository;

    public List<FamilyShare> getSharesByOwner(Long ownerUserId) {
        return familyShareRepository.findByOwnerUserIdOrderByCreatedAtDesc(ownerUserId);
    }

    @Transactional
    public String invite(Long ownerUserId, FamilyShareInviteForm form) {
        String token = UUID.randomUUID().toString();
        FamilyShare share = FamilyShare.builder()
                .ownerUserId(ownerUserId)
                .invitedEmail(form.getEmail())
                .shareRole(form.getShareRole())
                .permission(form.getPermission())
                .status("PENDING")
                .inviteToken(token)
                .build();
        familyShareRepository.save(share);
        return token;
    }

    @Transactional
    public void accept(String token, Long userId, String userEmail) {
        FamilyShare share = familyShareRepository.findByInviteToken(token)
                .orElseThrow(() -> new IllegalArgumentException("招待URLが無効です"));
        if (!share.getInvitedEmail().equalsIgnoreCase(userEmail)) {
            throw new IllegalArgumentException("この招待はあなた宛ではありません");
        }
        share.setSharedUserId(userId);
        share.setStatus("ACCEPTED");
        share.setInviteToken(null);
        familyShareRepository.save(share);
    }

    @Transactional
    public void revoke(Long shareId, Long ownerUserId) {
        FamilyShare share = familyShareRepository.findByIdAndOwnerUserId(shareId, ownerUserId)
                .orElseThrow(() -> new IllegalArgumentException("共有情報が見つかりません"));
        share.setStatus("REVOKED");
        familyShareRepository.save(share);
    }

    @Transactional
    public String resend(Long shareId, Long ownerUserId) {
        FamilyShare share = familyShareRepository.findByIdAndOwnerUserId(shareId, ownerUserId)
                .orElseThrow(() -> new IllegalArgumentException("共有情報が見つかりません"));
        String token = UUID.randomUUID().toString();
        share.setInviteToken(token);
        share.setStatus("PENDING");
        familyShareRepository.save(share);
        return token;
    }
}
