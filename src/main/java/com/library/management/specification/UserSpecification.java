package com.library.management.specification;

import com.library.management.core.filters.UserFilters;
import com.library.management.model.Role;
import com.library.management.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> build(UserFilters filters) {
        return Specification.allOf(
                hasSearch(filters.getSearch()),
                hasRole(filters.getRole()),
                isDeleted(false)
        );
    }

    private static Specification<User> hasSearch(String search) {
        return (root, query, cb) -> search == null || search.isBlank() ? cb.conjunction() :
                cb.like(cb.lower(root.get("username")), "%" + search.toLowerCase() + "%");
    }

    private static Specification<User> hasRole(String role) {
        return (root, query, cb) -> {
            if (role == null || role.isBlank()) return cb.conjunction();
            Join<User, Role> roleJoin = root.join("role");
            return cb.equal(roleJoin.get("name"), role);
        };
    }

    private static Specification<User> isDeleted(boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }
}