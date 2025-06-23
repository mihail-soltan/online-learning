package com.awbd.online_learning.controllers;

import com.awbd.online_learning.config.SecurityConfig;
import com.awbd.online_learning.dtos.CourseDTO;
import com.awbd.online_learning.services.CourseService;
import com.awbd.online_learning.services.ModuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModuleController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModuleService moduleService;
    @MockBean
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        // the controller fetches the course to display its title so we need to mock this
        given(courseService.findById(anyLong())).willReturn(CourseDTO.builder().id(1L).title("Test Course").build());
    }

    @Test
    @WithAnonymousUser
    void listModules_asAnonymousUser_shouldSucceed() throws Exception {
        // list view is public
        mockMvc.perform(get("/courses/1/modules"))
                .andExpect(status().isOk())
                .andExpect(view().name("module/module-list"))
                .andExpect(model().attributeExists("modules", "course"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void showCreateForm_asStudent_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/courses/1/modules/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showCreateForm_asAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(get("/courses/1/modules/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("module/module-form"));
    }
}
