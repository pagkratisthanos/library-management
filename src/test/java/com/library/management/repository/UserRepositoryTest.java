package com.library.management.repository;

import com.library.management.model.Role;
import com.library.management.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);

        user = new User();
        user.setUsername("admin");
        user.setPassword("$2a$10$hashedpassword");
        role.addUser(user);
        userRepository.save(user);
    }

    @Test
    void findByUsername_whenExists_shouldReturnUser() {
        Optional<User> found = userRepository.findByUsername("admin");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("admin");
    }

    @Test
    void findByUsername_whenNotExists_shouldReturnEmpty() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        assertThat(found).isEmpty();
    }

    @Test
    void findByIdAndDeletedFalse_whenExists_shouldReturnUser() {
        Optional<User> found = userRepository.findByIdAndDeletedFalse(user.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("admin");
    }

    @Test
    void findByIdAndDeletedFalse_whenDeleted_shouldReturnEmpty() {
        user.softDelete();
        userRepository.save(user);

        Optional<User> found = userRepository.findByIdAndDeletedFalse(user.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void findByUsernameAndDeletedFalse_whenExists_shouldReturnUser() {
        Optional<User> found = userRepository.findByUsernameAndDeletedFalse("admin");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("admin");
    }

    @Test
    void findByUsernameAndDeletedFalse_whenDeleted_shouldReturnEmpty() {
        user.softDelete();
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsernameAndDeletedFalse("admin");
        assertThat(found).isEmpty();
    }
}