package com.awbd.online_learning.controllers;

import com.awbd.online_learning.config.SecurityConfig;
import com.awbd.online_learning.dtos.CourseDTO;
import com.awbd.online_learning.services.CourseService;
import com.awbd.online_learning.services.InstructorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;
    @MockBean
    private InstructorService instructorService; // also needed for the form

    @Test
    @WithAnonymousUser // Test public access
    void listCourses_asAnonymousUser_shouldSucceed() throws Exception {
        // --- GIVEN ---
        // prepare a mock Page<CourseDTO> to be returned by the service
        Page<CourseDTO> mockCoursePage = new PageImpl<>(
                Collections.singletonList(CourseDTO.builder().id(1L).title("Mock Course").build()), // some content
                PageRequest.of(0, 5), // matches default pageable if not overridden
                1 // Total elements
        );
        // stub the courseService.findAll method
        given(courseService.findAll(any(PageRequest.class))).willReturn(mockCoursePage);
        // --- END GIVEN ---

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("course/course-list"))
                .andExpect(model().attributeExists("coursePage")) // verify the attribute is present
                .andExpect(model().attribute("coursePage", mockCoursePage));
    }

    @Test
    @WithMockUser(roles = "STUDENT") // user has a role but not the required one
    void showCreateForm_asStudent_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/courses/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void showCreateForm_asInstructor_shouldSucceed() throws Exception {
        mockMvc.perform(get("/courses/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("course/course-form"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveCourse_asAdmin_withValidData_shouldRedirect() throws Exception {
        // Given
        CourseDTO dto = CourseDTO.builder().title("Science").description("Fun science").price(100.0).instructorId(1L).build();
        given(courseService.save(any(CourseDTO.class))).willReturn(dto);

        // When / Then
        mockMvc.perform(post("/courses/save")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", dto.getTitle())
                        .param("description", dto.getDescription())
                        .param("price", dto.getPrice().toString())
                        .param("instructorId", dto.getInstructorId().toString())
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void deleteCourse_asInstructor_shouldSucceed() throws Exception {
        mockMvc.perform(get("/courses/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void deleteCourse_asStudent_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/courses/delete/1"))
                .andExpect(status().isForbidden());
    }
}
