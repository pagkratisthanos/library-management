package com.library.management.specification;

import com.library.management.core.filters.MemberFilters;
import com.library.management.model.Member;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpecification {

    public static Specification<Member> build(MemberFilters filters) {
        return Specification.allOf(
                hasFirstname(filters.getFirstname()),
                hasLastname(filters.getLastname()),
                hasEmail(filters.getEmail()),
                hasPhoneNumber(filters.getPhoneNumber()),
                isDeleted(false)
        );
    }

    private static Specification<Member> hasFirstname(String firstname) {
        return (root, query, cb) -> firstname == null ? cb.conjunction() :
                cb.like(cb.lower(root.get("firstname")), "%" + firstname.toLowerCase() + "%");
    }

    private static Specification<Member> hasLastname(String lastname) {
        return (root, query, cb) -> lastname == null ? cb.conjunction() :
                cb.like(cb.lower(root.get("lastname")), "%" + lastname.toLowerCase() + "%");
    }

    private static Specification<Member> hasEmail(String email) {
        return (root, query, cb) -> email == null ? cb.conjunction() :
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private static Specification<Member> hasPhoneNumber(String phoneNumber) {
        return (root, query, cb) -> phoneNumber == null ? cb.conjunction() :
                cb.like(root.get("phoneNumber"), "%" + phoneNumber + "%");
    }

    private static Specification<Member> isDeleted(boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }
}