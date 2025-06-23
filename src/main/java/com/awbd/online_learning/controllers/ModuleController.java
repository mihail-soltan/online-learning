package com.awbd.online_learning.controllers;

import com.awbd.online_learning.dtos.ModuleDTO;
import com.awbd.online_learning.services.CourseService;
import com.awbd.online_learning.services.ModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}/modules") // nested URL
public class ModuleController {

    private final ModuleService moduleService;
    private final CourseService courseService; // get course details for views

    @GetMapping
    public String listModulesForCourse(@PathVariable Long courseId, Model model) {
        model.addAttribute("modules", moduleService.findByCourseId(courseId));
        model.addAttribute("course", courseService.findById(courseId)); // displaying course title etc.
        return "module/module-list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public String showCreateForm(@PathVariable Long courseId, Model model) {
        ModuleDTO newModule = ModuleDTO.builder().courseId(courseId).build();
        model.addAttribute("module", newModule);
        model.addAttribute("courseId", courseId);
        model.addAttribute("pageTitle", "Add New Module");
        return "module/module-form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public String saveModule(@PathVariable Long courseId,
                             @Valid @ModelAttribute("module") ModuleDTO moduleDTO,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("courseId", courseId);
            model.addAttribute("pageTitle", moduleDTO.getId() == null ? "Add New Module" : "Edit Module");
            return "module/module-form";
        }

        moduleDTO.setCourseId(courseId);
        moduleService.save(moduleDTO);
        redirectAttributes.addFlashAttribute("message", "Module saved successfully!");
        return "redirect:/courses/" + courseId + "/modules";
    }

    @GetMapping("/edit/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public String showEditForm(@PathVariable Long courseId, @PathVariable Long moduleId, Model model) {
        model.addAttribute("module", moduleService.findById(moduleId));
        model.addAttribute("courseId", courseId);
        model.addAttribute("pageTitle", "Edit Module");
        return "module/module-form";
    }

    @GetMapping("/delete/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public String deleteModule(@PathVariable Long courseId, @PathVariable Long moduleId, RedirectAttributes redirectAttributes) {
        try {
            moduleService.deleteById(moduleId);
            redirectAttributes.addFlashAttribute("message", "Module deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error_message", "Could not delete module.");
        }
        return "redirect:/courses/" + courseId + "/modules";
    }
}
