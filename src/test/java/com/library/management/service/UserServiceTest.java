package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.core.filters.UserFilters;
import com.library.management.dto.PasswordChangeDTO;
import com.library.management.dto.UserInsertDTO;
import com.library.management.dto.UserPasswordUpdateDTO;
import com.library.management.dto.UserRoleUpdateDTO;
import com.library.management.model.Role;
import com.library.management.model.User;
import com.library.management.repository.RoleRepository;
import com.library.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Role adminRole;
    private Role librarianRole;
    private User existingUser;

    @BeforeEach
    void setUp() {
        adminRole = new Role();
        adminRole.setName("ADMIN");
        roleRepository.save(adminRole);

        librarianRole = new Role();
        librarianRole.setName("LIBRARIAN");
        roleRepository.save(librarianRole);

        existingUser = new User();
        existingUser.setUsername("admin");
        existingUser.setPassword("$2a$10$hashedpassword");
        adminRole.addUser(existingUser);
        userRepository.save(existingUser);
    }

    /** Goes through the service so the password is encoded with the real encoder. */
    private User createLibrarian() throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        return userService.saveUser(
                new UserInsertDTO("librarian1", "Librarian1!", librarianRole.getId()));
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

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        Page<User> users = userService.getAllUsers(new UserFilters(), PageRequest.of(0, 10));
        assertThat(users).isNotNull();
        assertThat(users.getContent()).hasSize(1);
    }

    @Test
    void getAllUsers_withSearch_shouldReturnOnlyMatchingUsers() {
        User librarian = new User();
        librarian.setUsername("librarian1");
        librarian.setPassword("$2a$10$hashedpassword");
        adminRole.addUser(librarian);
        userRepository.save(librarian);

        Page<User> users = userService.getAllUsers(
                UserFilters.builder().search("libr").build(),
                PageRequest.of(0, 10));

        assertThat(users.getContent()).hasSize(1);
        assertThat(users.getContent().get(0).getUsername()).isEqualTo("librarian1");
    }

    @Test
    void getAllUsers_withRoleFilter_shouldReturnOnlyUsersOfThatRole() {
        User librarian = new User();
        librarian.setUsername("librarian1");
        librarian.setPassword("$2a$10$hashedpassword");
        librarianRole.addUser(librarian);
        userRepository.save(librarian);

        Page<User> users = userService.getAllUsers(
                UserFilters.builder().role("LIBRARIAN").build(),
                PageRequest.of(0, 10));

        assertThat(users.getContent()).hasSize(1);
        assertThat(users.getContent().get(0).getUsername()).isEqualTo("librarian1");
    }

    @Test
    void getAllUsers_shouldNotReturnDeletedUsers() throws EntityNotFoundException {
        userService.deleteUserByUuid(existingUser.getId());

        Page<User> users = userService.getAllUsers(new UserFilters(), PageRequest.of(0, 10));

        assertThat(users.getContent()).isEmpty();
    }

    @Test
    void updateUserRole_whenPromotingALibrarian_shouldChangeTheRole()
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        User librarian = createLibrarian();

        User updated = userService.updateUserRole(
                librarian.getId(), new UserRoleUpdateDTO(adminRole.getId()));

        assertThat(updated.getRole().getName()).isEqualTo("ADMIN");
    }

    @Test
    void updateUserRole_whenDemotingTheLastAdmin_shouldThrowException() {
        assertThatThrownBy(() -> userService.updateUserRole(
                existingUser.getId(), new UserRoleUpdateDTO(librarianRole.getId())))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void updateUserRole_whenAnotherAdminExists_shouldAllowTheDemotion()
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        userService.saveUser(new UserInsertDTO("admin2", "Admin2Pass!", adminRole.getId()));

        User updated = userService.updateUserRole(
                existingUser.getId(), new UserRoleUpdateDTO(librarianRole.getId()));

        assertThat(updated.getRole().getName()).isEqualTo("LIBRARIAN");
    }

    @Test
    void updateUserRole_whenRoleDoesNotExist_shouldThrowException() {
        assertThatThrownBy(() -> userService.updateUserRole(
                existingUser.getId(), new UserRoleUpdateDTO(999L)))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void updateUserRole_whenUserNotFound_shouldThrowException() {
        assertThatThrownBy(() -> userService.updateUserRole(
                UUID.randomUUID(), new UserRoleUpdateDTO(adminRole.getId())))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateUserPassword_shouldStoreTheNewPasswordEncoded()
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        User librarian = createLibrarian();

        User updated = userService.updateUserPassword(
                librarian.getId(), new UserPasswordUpdateDTO("ResetPass1!"));

        assertThat(updated.getPassword()).isNotEqualTo("ResetPass1!");
        assertThat(passwordEncoder.matches("ResetPass1!", updated.getPassword())).isTrue();
    }

    @Test
    void updateUserPassword_whenUserNotFound_shouldThrowException() {
        assertThatThrownBy(() -> userService.updateUserPassword(
                UUID.randomUUID(), new UserPasswordUpdateDTO("ResetPass1!")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void changeOwnPassword_whenValid_shouldChangeThePassword()
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        createLibrarian();

        User updated = userService.changeOwnPassword(
                "librarian1", new PasswordChangeDTO("Librarian1!", "MyOwnPass1!"));

        assertThat(passwordEncoder.matches("MyOwnPass1!", updated.getPassword())).isTrue();
    }

    @Test
    void changeOwnPassword_whenCurrentPasswordIsWrong_shouldThrowException()
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        createLibrarian();

        assertThatThrownBy(() -> userService.changeOwnPassword(
                "librarian1", new PasswordChangeDTO("WrongPass1!", "MyOwnPass1!")))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void changeOwnPassword_whenNewPasswordEqualsTheCurrentOne_shouldThrowException()
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        createLibrarian();

        assertThatThrownBy(() -> userService.changeOwnPassword(
                "librarian1", new PasswordChangeDTO("Librarian1!", "Librarian1!")))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void changeOwnPassword_whenUserNotFound_shouldThrowException() {
        assertThatThrownBy(() -> userService.changeOwnPassword(
                "nobody", new PasswordChangeDTO("Librarian1!", "MyOwnPass1!")))
                .isInstanceOf(EntityNotFoundException.class);
    }
}