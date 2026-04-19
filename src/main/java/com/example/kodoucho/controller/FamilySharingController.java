package com.example.kodoucho.controller;

import com.example.kodoucho.dto.FamilyShareInviteForm;
import com.example.kodoucho.security.LoginUser;
import com.example.kodoucho.service.FamilySharingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class FamilySharingController {

    private final FamilySharingService familySharingService;
    private final LoginUser loginUser;

    @GetMapping("/family/sharing")
    public String showSharing(Model model, Authentication auth) {
        var user = loginUser.get(auth);
        model.addAttribute("shares", familySharingService.getSharesByOwner(user.getId()));
        model.addAttribute("inviteForm", new FamilyShareInviteForm());
        model.addAttribute("user", user);
        return "family/sharing";
    }

    @PostMapping("/family/sharing/invite")
    public String invite(@Valid @ModelAttribute FamilyShareInviteForm inviteForm,
                         BindingResult result, Model model, Authentication auth,
                         RedirectAttributes redirectAttributes) {
        var user = loginUser.get(auth);
        if (result.hasErrors()) {
            model.addAttribute("shares", familySharingService.getSharesByOwner(user.getId()));
            model.addAttribute("user", user);
            return "family/sharing";
        }
        String token = familySharingService.invite(user.getId(), inviteForm);
        redirectAttributes.addFlashAttribute("inviteToken", token);
        redirectAttributes.addFlashAttribute("successMessage", "招待URLを生成しました");
        return "redirect:/family/sharing";
    }

    @GetMapping("/family/sharing/accept/{token}")
    public String accept(@PathVariable String token, Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login?next=/family/sharing/accept/" + token;
        }
        try {
            var user = loginUser.get(auth);
            familySharingService.accept(token, user.getId(), user.getEmail());
            model.addAttribute("message", "招待を承諾しました");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "family/accept";
    }

    @PostMapping("/family/sharing/{id}/revoke")
    public String revoke(@PathVariable Long id, Authentication auth) {
        var user = loginUser.get(auth);
        familySharingService.revoke(id, user.getId());
        return "redirect:/family/sharing";
    }

    @PostMapping("/family/sharing/{id}/resend")
    public String resend(@PathVariable Long id, Authentication auth,
                         RedirectAttributes redirectAttributes) {
        var user = loginUser.get(auth);
        String token = familySharingService.resend(id, user.getId());
        redirectAttributes.addFlashAttribute("inviteToken", token);
        redirectAttributes.addFlashAttribute("successMessage", "新しい招待URLを生成しました");
        return "redirect:/family/sharing";
    }
}
