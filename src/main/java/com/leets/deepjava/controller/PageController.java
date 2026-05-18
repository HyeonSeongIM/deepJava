package com.leets.deepjava.controller;

import com.leets.deepjava.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final ProjectService projectService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/projects")
    public String projects(@RequestParam(required = false) String category, Model model) {
        if (category != null && !category.isBlank()) {
            model.addAttribute("projects", projectService.findByCategory(category));
            model.addAttribute("currentCategory", category);
        } else {
            model.addAttribute("projects", projectService.findAll());
            model.addAttribute("currentCategory", "ALL");
        }
        return "projects";
    }

    @GetMapping("/projects/{id}")
    public String projectDetail(@PathVariable Long id, Model model) {
        model.addAttribute("project", projectService.findById(id));
        return "project-detail";
    }
}
