package com.library.management.specification;

import com.library.management.core.filters.CopyFilters;
import com.library.management.model.Copy;
import com.library.management.model.CopyCondition;
import org.springframework.data.jpa.domain.Specification;

public class CopySpecification {

    public static Specification<Copy> build(CopyFilters filters) {
        return Specification.allOf(
                hasAvailable(filters.getAvailable()),
                hasCondition(filters.getCondition()),
                isDeleted(false)
        );
    }

    private static Specification<Copy> hasAvailable(Boolean available) {
        return (root, query, cb) -> available == null ? cb.conjunction() :
                cb.equal(root.get("available"), available);
    }

    private static Specification<Copy> hasCondition(CopyCondition condition) {
        return (root, query, cb) -> condition == null ? cb.conjunction() :
                cb.equal(root.get("condition"), condition);
    }

    private static Specification<Copy> isDeleted(boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }
}