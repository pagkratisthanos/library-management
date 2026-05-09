package com.library.management.model;

import jakarta.persistence.*;
import lombok.*;

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

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(mappedBy = "books")
    private Set<Author> authors = new HashSet<>();

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
