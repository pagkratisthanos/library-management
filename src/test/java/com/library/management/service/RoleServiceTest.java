package com.library.management.service;

import com.library.management.model.Role;
import com.library.management.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class RoleServiceTest {

    @Autowired
    private IRoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roleRepository.save(adminRole);

        Role librarianRole = new Role();
        librarianRole.setName("LIBRARIAN");
        roleRepository.save(librarianRole);
    }

    @Test
    void getAllRoles_shouldReturnAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        assertThat(roles).hasSize(2);
    }

    @Test
    void getAllRoles_shouldReturnRolesWithCorrectNames() {
        List<Role> roles = roleService.getAllRoles();
        assertThat(roles)
                .extracting(Role::getName)
                .containsExactlyInAnyOrder("ADMIN", "LIBRARIAN");
    }
}