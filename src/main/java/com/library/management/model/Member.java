package com.library.management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "members")
@ToString(exclude = {"address", "rentals"})
public class Member extends AbstractEntity {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "membership_date", nullable = false)
    private LocalDate membershipDate;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Rental> rentals = new ArrayList<>();

    public List<Rental> getAllRentals() {
        return Collections.unmodifiableList(rentals);
    }

    public void addRental(Rental rental) {
        rentals.add(rental);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return Objects.equals(getId(), member.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
