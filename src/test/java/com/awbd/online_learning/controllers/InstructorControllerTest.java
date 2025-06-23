package com.awbd.online_learning.controllers;

import com.awbd.online_learning.config.SecurityConfig;
import com.awbd.online_learning.dtos.InstructorDTO;
import com.awbd.online_learning.services.InstructorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstructorController.class)
@Import(SecurityConfig.class)

@ActiveProfiles("test")
public class InstructorControllerTest {

    @Autowired
    private MockMvc mockMvc; // for HTTP requests

    @MockBean // creates a Mockito mock for InstructorService and adds it to the application context
    private InstructorService instructorService;

    @Autowired
    private ObjectMapper objectMapper; // for converting objects to JSON

    @Test
    @WithMockUser // Simulates authenticated user (default user/pass, no specific roles)
        // for public endpoints
    void listInstructors_shouldReturnInstructorsPage() throws Exception {
        // Given
        Page<InstructorDTO> instructorPage = new PageImpl<>(
                Collections.singletonList(InstructorDTO.builder().id(1L).firstName("Test").build()),
                PageRequest.of(0, 5),
                1
        );
        given(instructorService.findAll(any(PageRequest.class))).willReturn(instructorPage);

        // When / Then
        mockMvc.perform(MockMvcRequestBuilders.get("/instructors"))
                .andExpect(status().isOk())
                .andExpect(view().name("instructor/instructor-list"))
                .andExpect(model().attributeExists("instructorPage"))
                .andExpect(model().attribute("instructorPage", hasProperty("content", hasSize(1))));
    }

    @Test
    @WithMockUser(roles = "ADMIN") // simulate a user with ROLE_ADMIN
    void showCreateForm_asAdmin_shouldReturnFormView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/instructors/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("instructor/instructor-form"))
                .andExpect(model().attributeExists("instructor"));
    }

    @Test
    @WithMockUser(roles = "STUDENT") // simulate a user with ROLE_STUDENT
    void showCreateForm_asStudent_shouldBeForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/instructors/new"))
                .andExpect(status().isForbidden()); // expecting 403 due to @PreAuthorize
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveInstructor_asAdmin_withValidData_shouldRedirect() throws Exception {
        // Given
        InstructorDTO dtoToSave = InstructorDTO.builder().firstName("Valid").lastName("Instructor").email("valid@e.com").bio("B").build();
        given(instructorService.save(any(InstructorDTO.class))).willReturn(dtoToSave); // assume save returns the saved DTO

        // When / Then
        mockMvc.perform(MockMvcRequestBuilders.post("/instructors/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", dtoToSave.getFirstName())
                        .param("lastName", dtoToSave.getLastName())
                        .param("email", dtoToSave.getEmail())
                        .param("bio", dtoToSave.getBio())
                        .with(csrf()) // include CSRF token for POST requests
                )
                .andExpect(status().is3xxRedirection()) // Expect redirect
                .andExpect(redirectedUrl("/instructors"))
                .andExpect(flash().attributeExists("message"));

        verify(instructorService).save(any(InstructorDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveInstructor_asAdmin_withInvalidData_shouldReturnFormWithError() throws Exception {
        // When / Then
        mockMvc.perform(MockMvcRequestBuilders.post("/instructors/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "") // Invalid: blank first name
                        .param("lastName", "Doe")
                        .param("email", "invalidemail") // Invalid email
                        .param("bio", "Test")
                        .with(csrf())
                )
                .andExpect(status().isOk()) // stays on the form
                .andExpect(view().name("instructor/instructor-form"))
                .andExpect(model().hasErrors()) // check for binding errors
                .andExpect(model().attributeHasFieldErrors("instructor", "firstName"))
                .andExpect(model().attributeHasFieldErrorCode("instructor", "email", "Email"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteInstructor_asAdmin_shouldRedirect() throws Exception {
        // Given
        doNothing().when(instructorService).deleteById(anyLong());

        // When / Then
        mockMvc.perform(MockMvcRequestBuilders.get("/instructors/delete/1")
                        // .with(csrf()) // GET requests typically don't need CSRF unless configured differently
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/instructors"))
                .andExpect(flash().attributeExists("message"));

        verify(instructorService).deleteById(1L);
    }

    // Add tests for edit form, and for users without ADMIN role trying to access protected endpoints
}