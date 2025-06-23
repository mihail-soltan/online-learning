package com.awbd.online_learning.controllers;

import com.awbd.online_learning.dtos.CourseDTO;
import com.awbd.online_learning.services.CourseService;
import com.awbd.online_learning.services.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final InstructorService instructorService; // Needed to select instructor
    private static final int DEFAULT_PAGE_SIZE = 5;

    @GetMapping
    public String listCourses(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size,
                              @RequestParam(defaultValue = "title") String sortField,
                              @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CourseDTO> coursePage = courseService.findAll(pageable);

        model.addAttribute("coursePage", coursePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "course/course-list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new CourseDTO());
        // list of instructors for selection
        model.addAttribute("allInstructors", instructorService.findAll());
        model.addAttribute("pageTitle", "Add New Course");
        return "course/course-form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public String saveCourse(@Valid @ModelAttribute("course") CourseDTO courseDTO,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {

            model.addAttribute("allInstructors", instructorService.findAll());
            model.addAttribute("pageTitle", courseDTO.getId() == null ? "Add New Course" : "Edit Course");
            return "course/course-form";
        }
        courseService.save(courseDTO);
        redirectAttributes.addFlashAttribute("message", "Course saved successfully!");
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        CourseDTO courseDTO = courseService.findById(id);
        model.addAttribute("course", courseDTO);
        model.addAttribute("allInstructors", instructorService.findAll());
        model.addAttribute("pageTitle", "Edit Course");
        return "course/course-form";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Course deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error_message", "Could not delete course. It might be associated with other records.");
        }
        return "redirect:/courses";
    }
}
