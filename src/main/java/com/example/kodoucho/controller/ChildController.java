package com.example.kodoucho.controller;

import com.example.kodoucho.dto.ChildForm;
import com.example.kodoucho.entity.Child;
import com.example.kodoucho.security.LoginUser;
import com.example.kodoucho.service.ChildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;
    private final LoginUser loginUser;

    @GetMapping("/family")
    public String family(Model model, Authentication auth) {
        var user = loginUser.get(auth);
        List<Child> children = childService.findAllByParent(user.getId());
        Map<Long, Integer> balances = children.stream()
                .collect(Collectors.toMap(Child::getId, c -> childService.calcBalance(c.getId())));
        Map<Long, String> gradeLabels = children.stream()
                .collect(Collectors.toMap(Child::getId, c -> childService.getGradeLabel(c.getBirthDate())));
        model.addAttribute("children", children);
        model.addAttribute("balances", balances);
        model.addAttribute("gradeLabels", gradeLabels);
        model.addAttribute("user", user);
        return "family/index";
    }

    @GetMapping("/children/new")
    public String showNew(Model model, Authentication auth) {
        model.addAttribute("childForm", new ChildForm());
        model.addAttribute("user", loginUser.get(auth));
        return "child/form";
    }

    @PostMapping("/children")
    public String create(@Valid @ModelAttribute ChildForm childForm, BindingResult result,
                         Model model, Authentication auth) {
        var user = loginUser.get(auth);
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "child/form";
        }
        childService.create(user.getId(), childForm);
        return "redirect:/family";
    }

    @GetMapping("/children/{id}/edit")
    public String showEdit(@PathVariable Long id, Model model, Authentication auth) {
        var user = loginUser.get(auth);
        Child child = childService.findByIdAndParent(id, user.getId());
        ChildForm form = new ChildForm();
        form.setName(child.getName());
        form.setAvatar(child.getAvatar());
        form.setThemeColor(child.getThemeColor());
        form.setBirthDate(child.getBirthDate());
        model.addAttribute("childForm", form);
        model.addAttribute("child", child);
        model.addAttribute("user", user);
        return "child/form";
    }

    @PostMapping("/children/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute ChildForm childForm,
                         BindingResult result, Model model, Authentication auth) {
        var user = loginUser.get(auth);
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "child/form";
        }
        childService.update(id, user.getId(), childForm);
        return "redirect:/family";
    }

    @PostMapping("/children/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth) {
        var user = loginUser.get(auth);
        childService.delete(id, user.getId());
        return "redirect:/family";
    }
}
