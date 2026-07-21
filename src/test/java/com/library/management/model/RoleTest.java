package com.library.management.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    private Role role;
    private User user;
    private Capability capability;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("ADMIN");

        user = new User();
        user.setUsername("admin");
        user.setPassword("password");

        capability = new Capability();
        capability.setName("VIEW_AUTHOR");
    }

    @Test
    void addUser_shouldAddUserToRole() {
        role.addUser(user);
        assertThat(role.getAllUsers()).contains(user);
    }

    @Test
    void addUser_shouldSyncRelationship() {
        role.addUser(user);
        assertThat(user.getRole()).isEqualTo(role);
    }

    @Test
    void removeUser_shouldRemoveUserFromRole() {
        role.addUser(user);
        role.removeUser(user);
        assertThat(role.getAllUsers()).doesNotContain(user);
    }

    @Test
    void removeUser_shouldSyncRelationship() {
        role.addUser(user);
        role.removeUser(user);
        assertThat(user.getRole()).isNull();
    }

    @Test
    void addUsers_shouldAddMultipleUsers() {
        User user2 = new User();
        user2.setUsername("librarian");
        user2.setPassword("password");

        role.addUsers(List.of(user, user2));
        assertThat(role.getAllUsers()).hasSize(2);
    }

    @Test
    void addCapability_shouldAddCapabilityToRole() {
        role.addCapability(capability);
        assertThat(role.getAllCapabilities()).contains(capability);
    }

    @Test
    void addCapability_shouldSyncRelationship() {
        role.addCapability(capability);
        assertThat(capability.getAllRoles()).contains(role);
    }

    @Test
    void removeCapability_shouldRemoveCapabilityFromRole() {
        role.addCapability(capability);
        role.removeCapability(capability);
        assertThat(role.getAllCapabilities()).doesNotContain(capability);
    }

    @Test
    void equals_whenSameName_shouldReturnTrue() {
        Role anotherRole = new Role();
        anotherRole.setName("ADMIN");
        assertThat(role).isEqualTo(anotherRole);
    }

    @Test
    void equals_whenDifferentName_shouldReturnFalse() {
        Role anotherRole = new Role();
        anotherRole.setName("LIBRARIAN");
        assertThat(role).isNotEqualTo(anotherRole);
    }
}