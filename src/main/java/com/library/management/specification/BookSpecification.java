package com.library.management.specification;

import com.library.management.core.filters.BookFilters;
import com.library.management.model.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> build(BookFilters filters) {
        return Specification.allOf(
                hasTitle(filters.getTitle()),
                hasIsbn(filters.getIsbn()),
                hasLanguage(filters.getLanguage()),
                hasDescription(filters.getDescription()),
                isDeleted(false)
        );
    }

    private static Specification<Book> hasTitle(String title) {
        return (root, query, cb) -> title == null ? cb.conjunction() :
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    private static Specification<Book> hasIsbn(String isbn) {
        return (root, query, cb) -> isbn == null ? cb.conjunction() :
                cb.like(root.get("isbn"), "%" + isbn + "%");
    }

    private static Specification<Book> hasLanguage(String language) {
        return (root, query, cb) -> language == null ? cb.conjunction() :
                cb.like(cb.lower(root.get("language")), "%" + language.toLowerCase() + "%");
    }

    private static Specification<Book> hasDescription(String description) {
        return (root, query, cb) -> description == null ? cb.conjunction() :
                cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    private static Specification<Book> isDeleted(boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }
}