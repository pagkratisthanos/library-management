package com.library.management.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "copies")
@ToString(exclude = {"rentals", "book"})
public class Copy extends AbstractEntity {

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "copy", fetch = FetchType.LAZY)
    private List<Rental> rentals = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Boolean available;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CopyCondition condition;

    public List<Rental> getAllRentals() {
        return Collections.unmodifiableList(rentals);
    }

    public void addRental(Rental rental) {
        rentals.add(rental);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Copy)) return false;
        Copy copy = (Copy) o;
        return Objects.equals(getId(), copy.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
