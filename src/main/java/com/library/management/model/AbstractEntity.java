package com.library.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * What every entity in the model has in common: a UUID key, audit timestamps, and a soft-delete
 * flag.
 *
 * <p><b>Nothing is ever really deleted.</b> {@link #softDelete()} sets a flag, the row stays, and
 * history that points at it stays valid — a returned rental still names the copy it was for. The
 * cost is that every read path has to exclude deleted rows itself. That is why the repositories and
 * services come in pairs, one variant seeing everything and one filtered by {@code deleted = false},
 * and why a query written without that filter is a bug rather than a style choice.
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {

    /**
     * Assigned in Java at construction rather than by the database. The key therefore exists before
     * the entity is persisted, which lets an object graph be wired up in memory and saved in one
     * go, and keeps inserts from having to round-trip for a generated value.
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant updatedAt;

    @Column(nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant deletedAt;

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = Instant.now();
    }
}