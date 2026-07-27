package com.library.management.api;

import com.library.management.dto.RoleReadOnlyDTO;
import com.library.management.mapper.RoleMapper;
import com.library.management.model.Role;
import com.library.management.service.IRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RoleRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRoleService roleService;

    @MockitoBean
    private RoleMapper roleMapper;

    private Role adminRole;
    private Role librarianRole;

    @BeforeEach
    void setUp() {
        adminRole = new Role();
        adminRole.setName("ADMIN");

        librarianRole = new Role();
        librarianRole.setName("LIBRARIAN");
    }

    @Test
    void getAllRoles_shouldReturn200WithRoles() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of(adminRole, librarianRole));
        when(roleMapper.mapToRoleReadOnlyDTO(adminRole)).thenReturn(new RoleReadOnlyDTO(1L, "ADMIN"));
        when(roleMapper.mapToRoleReadOnlyDTO(librarianRole)).thenReturn(new RoleReadOnlyDTO(2L, "LIBRARIAN"));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ADMIN"))
                .andExpect(jsonPath("$[1].name").value("LIBRARIAN"));
    }

    @Test
    void getAllRoles_whenEmpty_shouldReturn200WithEmptyList() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of());

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}