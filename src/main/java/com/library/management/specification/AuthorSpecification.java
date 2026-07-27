package com.library.management.specification;

import com.library.management.core.filters.AuthorFilters;
import com.library.management.model.Author;
import org.springframework.data.jpa.domain.Specification;

public class AuthorSpecification {

    public static Specification<Author> build(AuthorFilters filters) {
        return Specification.allOf(
                hasFirstname(filters.getFirstname()),
                hasLastname(filters.getLastname()),
                hasBirthPlace(filters.getBirthPlace()),
                isDeleted(false)
        );
    }

    private static Specification<Author> hasFirstname(String firstname) {
        return (root, query, cb) -> firstname == null ? cb.conjunction() :
                cb.like(cb.lower(root.get("firstname")), "%" + firstname.toLowerCase() + "%");
    }

    private static Specification<Author> hasLastname(String lastname) {
        return (root, query, cb) -> lastname == null ? cb.conjunction() :
                cb.like(cb.lower(root.get("lastname")), "%" + lastname.toLowerCase() + "%");
    }

    private static Specification<Author> hasBirthPlace(String birthPlace) {
        return (root, query, cb) -> birthPlace == null ? cb.conjunction() :
                cb.like(cb.lower(root.get("birthPlace")), "%" + birthPlace.toLowerCase() + "%");
    }

    private static Specification<Author> isDeleted(boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }
}