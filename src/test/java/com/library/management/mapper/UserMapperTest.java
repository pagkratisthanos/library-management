package com.library.management.mapper;

import com.library.management.dto.UserReadOnlyDTO;
import com.library.management.model.Role;
import com.library.management.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper userMapper;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();

        role = new Role();
        role.setName("ADMIN");

        user = new User();
        user.setUsername("admin");
        user.setPassword("password");
        role.addUser(user);
    }

    @Test
    void mapToUserReadOnlyDTO_shouldMapCorrectly() {
        UserReadOnlyDTO dto = userMapper.mapToUserReadOnlyDTO(user);

        assertThat(dto.id()).isEqualTo(user.getId());
        assertThat(dto.username()).isEqualTo("admin");
        assertThat(dto.role()).isEqualTo("ADMIN");
    }
}