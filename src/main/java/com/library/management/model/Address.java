package com.library.management.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "addresses")
@ToString(exclude = "member")
public class Address extends AbstractEntity{

    @Column(nullable = false)
    private String street;

    @Column(name = "street_number", nullable = false)
    private String streetNumber;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @OneToOne(mappedBy = "address")
    private Member member;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Address address)) return false;
        return Objects.equals(getId(), address.getId());
    }



    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
