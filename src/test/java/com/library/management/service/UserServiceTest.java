package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.UserInsertDTO;
import com.library.management.model.Role;
import com.library.management.model.User;
import com.library.management.repository.RoleRepository;
import com.library.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class UserServiceTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role adminRole;
    private User existingUser;

    @BeforeEach
    void setUp() {
        adminRole = new Role();
        adminRole.setName("ADMIN");
        roleRepository.save(adminRole);

        existingUser = new User();
        existingUser.setUsername("admin");
        existingUser.setPassword("$2a$10$hashedpassword");
        adminRole.addUser(existingUser);
        userRepository.save(existingUser);
    }

    @Test
    void saveUser_whenValidData_shouldSaveAndReturnUser()
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        UserInsertDTO dto = new UserInsertDTO("librarian1", "Librarian1!", adminRole.getId());

        User saved = userService.saveUser(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("librarian1");
        assertThat(saved.getRole().getName()).isEqualTo("ADMIN");
    }

    @Test
    void saveUser_whenUsernameAlreadyExists_shouldThrowException() {
        UserInsertDTO dto = new UserInsertDTO("admin", "Admin123!", adminRole.getId());

        assertThatThrownBy(() -> userService.saveUser(dto))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    void saveUser_whenRoleNotFound_shouldThrowException() {
        UserInsertDTO dto = new UserInsertDTO("newuser", "NewUser1!", 999L);

        assertThatThrownBy(() -> userService.saveUser(dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void getUserByUuid_whenExists_shouldReturnUser() throws EntityNotFoundException {
        User found = userService.getUserByUuid(existingUser.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("admin");
    }

    @Test
    void getUserByUuid_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> userService.getUserByUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getUserByUuidDeletedFalse_whenExists_shouldReturnUser() throws EntityNotFoundException {
        User found = userService.getUserByUuidDeletedFalse(existingUser.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("admin");
    }

    @Test
    void getUserByUuidDeletedFalse_whenDeleted_shouldThrowException() {
        existingUser.softDelete();
        userRepository.save(existingUser);

        assertThatThrownBy(() -> userService.getUserByUuidDeletedFalse(existingUser.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getUserByUuidDeletedFalse_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> userService.getUserByUuidDeletedFalse(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void isUserExists_whenExists_shouldReturnTrue() {
        boolean exists = userService.isUserExists("admin");
        assertThat(exists).isTrue();
    }

    @Test
    void isUserExists_whenNotExists_shouldReturnFalse() {
        boolean exists = userService.isUserExists("nonexistent");
        assertThat(exists).isFalse();
    }

    @Test
    void deleteUserByUuid_whenExists_shouldSoftDelete() throws EntityNotFoundException {
        userService.deleteUserByUuid(existingUser.getId());

        User deleted = userRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(deleted.isDeleted()).isTrue();
        assertThat(deleted.getDeletedAt()).isNotNull();
    }

    @Test
    void deleteUserByUuid_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> userService.deleteUserByUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteUserByUuid_whenAlreadyDeleted_shouldThrowException() {
        existingUser.softDelete();
        userRepository.save(existingUser);

        assertThatThrownBy(() -> userService.deleteUserByUuid(existingUser.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}