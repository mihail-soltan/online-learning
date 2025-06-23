package com.awbd.online_learning.controllers;

import com.awbd.online_learning.config.SecurityConfig;
import com.awbd.online_learning.dtos.StudentDTO;
import com.awbd.online_learning.services.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    @WithAnonymousUser
    void listStudents_asAnonymous_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void listStudents_asInstructor_shouldSucceed() throws Exception {
        // mock the service call to prevent the template error
        Page<StudentDTO> studentPage = new PageImpl<>(Collections.singletonList(new StudentDTO()));
        given(studentService.findAll(any(Pageable.class))).willReturn(studentPage);

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student-list"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void listStudents_asAdmin_shouldSucceed() throws Exception {
        Page<StudentDTO> studentPage = new PageImpl<>(Collections.singletonList(new StudentDTO()));
        given(studentService.findAll(any(Pageable.class))).willReturn(studentPage);

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student-list"))
                .andExpect(model().attributeExists("studentPage"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStudent_asAdmin_shouldRedirect() throws Exception {
        doNothing().when(studentService).deleteById(1L);

        mockMvc.perform(get("/students/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));
    }
}
