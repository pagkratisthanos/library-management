package com.library.management.specification;

import com.library.management.core.filters.RentalFilters;
import com.library.management.model.Rental;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class RentalSpecification {

    public static Specification<Rental> build(RentalFilters filters) {
        return Specification.allOf(
                hasMember(filters.getMemberUuid()),
                hasCopy(filters.getCopyUuid()),
                isActive(filters.getActive())
        );
    }

    private static Specification<Rental> hasMember(UUID memberUuid) {
        return (root, query, cb) -> memberUuid == null ? cb.conjunction() :
                cb.equal(root.get("member").get("id"), memberUuid);
    }

    private static Specification<Rental> hasCopy(UUID copyUuid) {
        return (root, query, cb) -> copyUuid == null ? cb.conjunction() :
                cb.equal(root.get("copy").get("id"), copyUuid);
    }

    private static Specification<Rental> isActive(String active) {
        return (root, query, cb) -> {
            if (active == null || active.isBlank()) return cb.conjunction();
            boolean isActive = Boolean.parseBoolean(active);
            return isActive ? cb.isNull(root.get("returnDate")) :
                    cb.isNotNull(root.get("returnDate"));
        };
    }
}