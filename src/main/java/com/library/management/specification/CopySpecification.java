package com.library.management.specification;

import com.library.management.core.filters.CopyFilters;
import com.library.management.model.Book;
import com.library.management.model.Copy;
import com.library.management.model.CopyCondition;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class CopySpecification {

    public static Specification<Copy> build(CopyFilters filters) {
        return Specification.allOf(
                hasBookTitle(filters.getBookTitle()),
                hasAvailable(filters.getAvailable()),
                hasCondition(filters.getCondition()),
                isDeleted(false)
        );
    }

    /** Filters copies by the title of the book they belong to. */
    private static Specification<Copy> hasBookTitle(String bookTitle) {
        return (root, query, cb) -> {
            if (bookTitle == null || bookTitle.isBlank()) return cb.conjunction();

            Join<Copy, Book> book = root.join("book");
            return cb.like(cb.lower(book.get("title")), "%" + bookTitle.toLowerCase() + "%");
        };
    }

    private static Specification<Copy> hasAvailable(Boolean available) {
        return (root, query, cb) -> available == null ? cb.conjunction() :
                cb.equal(root.get("available"), available);
    }

    private static Specification<Copy> hasCondition(CopyCondition condition) {
        return (root, query, cb) -> condition == null ? cb.conjunction() :
                cb.equal(root.get("condition"), condition);
    }

    private static Specification<Copy> isDeleted(boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }
}