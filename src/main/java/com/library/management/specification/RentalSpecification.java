package com.library.management.specification;

import com.library.management.core.filters.RentalFilters;
import com.library.management.model.Book;
import com.library.management.model.Copy;
import com.library.management.model.Member;
import com.library.management.model.Rental;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class RentalSpecification {

    public static Specification<Rental> build(RentalFilters filters) {
        return Specification.allOf(
                hasSearch(filters.getSearch()),
                hasMember(filters.getMemberUuid()),
                hasCopy(filters.getCopyUuid()),
                isActive(filters.getActive())
        );
    }

    /** Free-text search across the member's name and the rented book's title. */
    private static Specification<Rental> hasSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return cb.conjunction();

            String pattern = "%" + search.toLowerCase() + "%";

            Join<Rental, Member> member = root.join("member");
            Join<Copy, Book> book = root.join("copy").join("book");

            return cb.or(
                    cb.like(cb.lower(member.get("firstname")), pattern),
                    cb.like(cb.lower(member.get("lastname")), pattern),
                    cb.like(cb.lower(book.get("title")), pattern)
            );
        };
    }

    private static Specification<Rental> hasMember(UUID memberUuid) {
        return (root, query, cb) -> memberUuid == null ? cb.conjunction() :
                cb.equal(root.get("member").get("id"), memberUuid);
    }

    private static Specification<Rental> hasCopy(UUID copyUuid) {
        return (root, query, cb) -> copyUuid == null ? cb.conjunction() :
                cb.equal(root.get("copy").get("id"), copyUuid);
    }

    private static Specification<Rental> isActive(String active) {
        return (root, query, cb) -> {
            if (active == null || active.isBlank()) return cb.conjunction();
            boolean isActive = Boolean.parseBoolean(active);
            return isActive ? cb.isNull(root.get("returnDate")) :
                    cb.isNotNull(root.get("returnDate"));
        };
    }
}