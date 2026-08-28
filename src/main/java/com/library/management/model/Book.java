package com.library.management.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "books")
@ToString(exclude = {"copies", "authors"})
public class Book extends AbstractEntity {

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private List<Copy> copies = new ArrayList<>();

    /**
     * Batched rather than fetched with an entity graph. A graph would join the authors into the
     * page query, and a join that multiplies rows cannot be combined with SQL {@code LIMIT} —
     * Hibernate would fall back to paginating in memory. {@code @BatchSize} keeps the page query
     * intact and loads the authors of all twenty books on the page in one extra query instead of
     * twenty.
     */
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(mappedBy = "books")
    @BatchSize(size = 20)
    private Set<Author> authors = new HashSet<>();

    /**
     * Copies of this book that have not been soft-deleted. Read-only, computed by the database.
     *
     * <p>A subquery rather than {@code copies.size()} so that it can be sorted and filtered on in
     * SQL, and so that listing a page of books does not have to load every copy of every book.
     */
    @Setter(AccessLevel.NONE)
    @Formula("(select count(c.id) from copies c where c.book_id = {alias}.id and c.deleted = false)")
    private long totalCopies;

    /** Copies that are not currently lent out. Read-only, computed by the database. */
    @Setter(AccessLevel.NONE)
    @Formula("(select count(c.id) from copies c where c.book_id = {alias}.id and c.deleted = false and c.available = true)")
    private long availableCopies;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    private String language;

    @Column(name = "daily_cost", nullable = false)
    private BigDecimal dailyCost;

    private String description;

    public Optional<Copy> getCopy(UUID copyId) {

        return copies.stream()
                .filter(copy -> copy.getId().equals(copyId))
                .findFirst();
    }

    public Set<Author> getAllAuthors() {
        return Collections.unmodifiableSet(authors);
    }

    public Optional<Author> getAuthor(UUID authorId) {
        return authors.stream()
                .filter(author -> author.getId().equals(authorId))
                .findFirst();
    }

    public void addAuthor(Author author) {
        authors.add(author);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
    }

    public List<Copy> getAllCopies() {
        return Collections.unmodifiableList(copies);
    }

    public void addCopy(Copy copy) {
        copies.add(copy);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(getId(), book.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
