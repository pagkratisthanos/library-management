package com.library.management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "rentals")
@ToString(exclude = {"member", "copy"})
public class Rental extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "copy_id", nullable = false)
    private Copy copy;

    @Column(name = "rental_date", nullable = false)
    private Instant rentalDate;

    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "return_date")
    private Instant returnDate;

    @PrePersist
    public void initializeUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rental)) return false;
        Rental rental = (Rental) o;
        return Objects.equals(getUuid(), rental.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUuid());
    }
}
