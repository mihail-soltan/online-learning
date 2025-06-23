package com.awbd.online_learning.controllers;

import com.awbd.online_learning.dtos.StudentDTO;
import com.awbd.online_learning.services.StudentService;
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
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @GetMapping
    public String listStudents(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size,
                               @RequestParam(defaultValue = "lastName") String sortField,
                               @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<StudentDTO> studentPage = studentService.findAll(pageable);

        model.addAttribute("studentPage", studentPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "student/student-list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new StudentDTO());
        model.addAttribute("pageTitle", "Add New Student");
        return "student/student-form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveStudent(@Valid @ModelAttribute("student") StudentDTO studentDTO,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", studentDTO.getId() == null ? "Add New Student" : "Edit Student");
            return "student/student-form";
        }
        studentService.save(studentDTO);
        redirectAttributes.addFlashAttribute("message", "Student saved successfully!");
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.findById(id));
        model.addAttribute("pageTitle", "Edit Student");
        return "student/student-form";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error_message", "Could not delete student. It might be associated with other records.");
        }
        return "redirect:/students";
    }
}
