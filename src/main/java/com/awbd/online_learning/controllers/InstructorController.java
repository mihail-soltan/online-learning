package com.awbd.online_learning.controllers;

import com.awbd.online_learning.dtos.InstructorDTO;
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
@RequestMapping("/instructors")
public class InstructorController {

    private final InstructorService instructorService;
    private static final int DEFAULT_PAGE_SIZE = 5;


    @GetMapping
    public String listInstructors(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size,
                                  @RequestParam(defaultValue = "lastName") String sortField,
                                  @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<InstructorDTO> instructorPage = instructorService.findAll(pageable);

        model.addAttribute("instructorPage", instructorPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "instructor/instructor-list";
    }

    // show form for creating new instructor
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("instructor", InstructorDTO.builder().build()); // empty DTO for the form
        model.addAttribute("pageTitle", "Add New Instructor");
        return "instructor/instructor-form";
    }

    // process the creation of a new instructor
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveInstructor(@Valid @ModelAttribute("instructor") InstructorDTO instructorDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", instructorDTO.getId() == null ? "Add New Instructor" : "Edit Instructor");
            return "instructor/instructor-form";
        }
        instructorService.save(instructorDTO);
        redirectAttributes.addFlashAttribute("message", "Instructor saved successfully!");
        return "redirect:/instructors";
    }

    // show form for editing an existing instructor
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        InstructorDTO instructorDTO = instructorService.findById(id);
        model.addAttribute("instructor", instructorDTO);
        model.addAttribute("pageTitle", "Edit Instructor");
        return "instructor/instructor-form";
    }

    // process deletion of an instructor
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteInstructor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            instructorService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Instructor deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error_message", "Could not delete instructor. It might be associated with other records.");
        }
        return "redirect:/instructors";
    }
}