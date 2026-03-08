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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PRIVATE)
    private List<Copy> copies = new ArrayList<>();

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

    @PrePersist
    public void initializeUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public List<Copy> getAllCopies() {
        return Collections.unmodifiableList(copies);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(getUuid(), book.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUuid());
    }
}
