package com.library.management.specification;

import com.library.management.core.filters.BookFilters;
import com.library.management.model.Author;
import com.library.management.model.Book;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> build(BookFilters filters) {
        return Specification.allOf(
                hasSearch(filters.getSearch()),
                hasTitle(filters.getTitle()),
                hasIsbn(filters.getIsbn()),
                hasLanguage(filters.getLanguage()),
                hasDescription(filters.getDescription()),
                isDeleted(false)
        );
    }

    /** Free-text search across the title, the ISBN and the authors' names. */
    private static Specification<Book> hasSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return cb.conjunction();

            String pattern = "%" + search.toLowerCase() + "%";

            // a book can have several authors, so the join would repeat the same book
            if (query != null) {
                query.distinct(true);
            }

            Join<Book, Author> author = root.join("authors", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("isbn")), pattern),
                    cb.like(cb.lower(author.get("firstname")), pattern),
                    cb.like(cb.lower(author.get("lastname")), pattern)
            );
        };
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