package com.example.kodoucho.controller;

import com.example.kodoucho.dto.GoalForm;
import com.example.kodoucho.entity.Goal;
import com.example.kodoucho.security.LoginUser;
import com.example.kodoucho.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final ChildService childService;
    private final LoginUser loginUser;

    @GetMapping("/children/{id}/goals/new")
    public String showNew(@PathVariable Long id, Model model, Authentication auth) {
        model.addAttribute("goalForm", new GoalForm());
        model.addAttribute("child", childService.findById(id));
        model.addAttribute("user", loginUser.get(auth));
        return "goal/form";
    }

    @PostMapping("/children/{id}/goals")
    public String create(@PathVariable Long id,
                         @Valid @ModelAttribute GoalForm goalForm,
                         BindingResult result, Model model, Authentication auth) {
        if (result.hasErrors()) {
            model.addAttribute("child", childService.findById(id));
            model.addAttribute("user", loginUser.get(auth));
            return "goal/form";
        }
        goalService.create(id, goalForm);
        return "redirect:/children/" + id;
    }

    @GetMapping("/children/{id}/goals/{goalId}/edit")
    public String showEdit(@PathVariable Long id, @PathVariable Long goalId,
                           Model model, Authentication auth) {
        Goal goal = goalService.findByIdAndChild(goalId, id);
        GoalForm form = new GoalForm();
        form.setName(goal.getName());
        form.setTargetAmount(goal.getTargetAmount());
        form.setTargetDate(goal.getTargetDate());
        form.setPurposeCategory(goal.getPurposeCategory());
        form.setMessage(goal.getMessage());
        form.setEmoji(goal.getEmoji());
        model.addAttribute("goalForm", form);
        model.addAttribute("child", childService.findById(id));
        model.addAttribute("goal", goal);
        model.addAttribute("user", loginUser.get(auth));
        return "goal/form";
    }

    @PostMapping("/children/{id}/goals/{goalId}")
    public String update(@PathVariable Long id, @PathVariable Long goalId,
                         @Valid @ModelAttribute GoalForm goalForm,
                         BindingResult result, Model model, Authentication auth) {
        if (result.hasErrors()) {
            model.addAttribute("child", childService.findById(id));
            model.addAttribute("user", loginUser.get(auth));
            return "goal/form";
        }
        goalService.update(goalId, id, goalForm);
        return "redirect:/children/" + id;
    }

    @PostMapping("/children/{id}/goals/{goalId}/delete")
    public String delete(@PathVariable Long id, @PathVariable Long goalId) {
        goalService.delete(goalId, id);
        return "redirect:/children/" + id;
    }
}
