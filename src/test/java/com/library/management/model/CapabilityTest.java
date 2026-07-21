package com.library.management.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CapabilityTest {

    private Capability capability;
    private Role role;

    @BeforeEach
    void setUp() {
        capability = new Capability();
        capability.setName("VIEW_AUTHOR");
        capability.setDescription("View author details");

        role = new Role();
        role.setName("ADMIN");
    }

    @Test
    void addRole_shouldAddRoleToCapability() {
        capability.addRole(role);
        assertThat(capability.getAllRoles()).contains(role);
    }

    @Test
    void addRole_shouldSyncRelationship() {
        capability.addRole(role);
        assertThat(role.getAllCapabilities()).contains(capability);
    }

    @Test
    void removeRole_shouldRemoveRoleFromCapability() {
        capability.addRole(role);
        capability.removeRole(role);
        assertThat(capability.getAllRoles()).doesNotContain(role);
    }

    @Test
    void removeRole_shouldSyncRelationship() {
        capability.addRole(role);
        capability.removeRole(role);
        assertThat(role.getAllCapabilities()).doesNotContain(capability);
    }

    @Test
    void getAllRoles_shouldReturnUnmodifiableSet() {
        capability.addRole(role);
        assertThat(capability.getAllRoles()).hasSize(1);
    }

    @Test
    void equals_whenSameName_shouldReturnTrue() {
        Capability anotherCapability = new Capability();
        anotherCapability.setName("VIEW_AUTHOR");
        assertThat(capability).isEqualTo(anotherCapability);
    }

    @Test
    void equals_whenDifferentName_shouldReturnFalse() {
        Capability anotherCapability = new Capability();
        anotherCapability.setName("DELETE_AUTHOR");
        assertThat(capability).isNotEqualTo(anotherCapability);
    }
}