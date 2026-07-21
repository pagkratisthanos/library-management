package com.library.management.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User user;
    private Role role;
    private Capability capability;

    @BeforeEach
    void setUp() {
        capability = new Capability();
        capability.setName("VIEW_AUTHOR");

        role = new Role();
        role.setName("ADMIN");
        role.addCapability(capability);

        user = new User();
        user.setUsername("admin");
        user.setPassword("password");
        role.addUser(user);
    }

    @Test
    void getAuthorities_shouldContainRoleAuthority() {
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN");
    }

    @Test
    void getAuthorities_shouldContainCapabilityAuthority() {
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .contains("VIEW_AUTHOR");
    }

    @Test
    void isAccountNonExpired_shouldReturnTrue() {
        assertThat(user.isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue() {
        assertThat(user.isAccountNonLocked()).isTrue();
    }

    @Test
    void isCredentialsNonExpired_shouldReturnTrue() {
        assertThat(user.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void isEnabled_whenNotDeleted_shouldReturnTrue() {
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void isEnabled_whenDeleted_shouldReturnFalse() {
        user.softDelete();
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    void softDelete_shouldMarkUserAsDeleted() {
        user.softDelete();
        assertThat(user.isDeleted()).isTrue();
        assertThat(user.getDeletedAt()).isNotNull();
    }

    @Test
    void equals_whenSameUsername_shouldReturnTrue() {
        User anotherUser = new User();
        anotherUser.setUsername("admin");
        assertThat(user).isEqualTo(anotherUser);
    }

    @Test
    void equals_whenDifferentUsername_shouldReturnFalse() {
        User anotherUser = new User();
        anotherUser.setUsername("librarian");
        assertThat(user).isNotEqualTo(anotherUser);
    }

    @Test
    void hashCode_shouldBeConsistent() {
        int hashCode1 = user.hashCode();
        int hashCode2 = user.hashCode();
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void equals_whenNull_shouldReturnFalse() {
        assertThat(user.equals(null)).isFalse();
    }

    @Test
    void equals_whenDifferentType_shouldReturnFalse() {
        assertThat(user.equals("string")).isFalse();
    }
}