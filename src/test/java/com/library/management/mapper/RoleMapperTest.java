package com.library.management.mapper;

import com.library.management.dto.RoleReadOnlyDTO;
import com.library.management.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleMapperTest {

    private RoleMapper roleMapper;
    private Role role;

    @BeforeEach
    void setUp() {
        roleMapper = new RoleMapper();

        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
    }

    @Test
    void mapToRoleReadOnlyDTO_shouldMapCorrectly() {
        RoleReadOnlyDTO dto = roleMapper.mapToRoleReadOnlyDTO(role);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("ADMIN");
    }
}