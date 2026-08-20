package com.library.management.repository;

import com.library.management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>,
        JpaSpecificationExecutor<User> {

    @EntityGraph(attributePaths = {"role", "role.capabilities"})
    Optional<User> findByUsername(String username);

    /** The mapper reads user.getRole().getName(). */
    @Override
    @EntityGraph(attributePaths = {"role"})
    Page<User> findAll(Specification<User> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"role"})
    Optional<User> findByIdAndDeletedFalse(UUID id);

    @EntityGraph(attributePaths = {"role"})
    Optional<User> findByUsernameAndDeletedFalse(String username);

    long countByRoleNameAndDeletedFalse(String roleName);
}