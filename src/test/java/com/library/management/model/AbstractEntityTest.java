package com.library.management.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractEntityTest {

    static class TestEntity extends AbstractEntity {}

    @Test
    void id_shouldBeGeneratedAutomatically() {
        TestEntity entity = new TestEntity();
        assertThat(entity.getId()).isNotNull();
    }

    @Test
    void twoEntities_shouldHaveDifferentIds() {
        TestEntity entity1 = new TestEntity();
        TestEntity entity2 = new TestEntity();
        assertThat(entity1.getId()).isNotEqualTo(entity2.getId());
    }

    @Test
    void softDelete_shouldSetDeletedTrue() {
        TestEntity entity = new TestEntity();
        assertThat(entity.isDeleted()).isFalse();

        entity.softDelete();

        assertThat(entity.isDeleted()).isTrue();
    }

    @Test
    void softDelete_shouldSetDeletedAt() {
        TestEntity entity = new TestEntity();
        assertThat(entity.getDeletedAt()).isNull();

        Instant before = Instant.now();
        entity.softDelete();
        Instant after = Instant.now();

        assertThat(entity.getDeletedAt()).isNotNull();
        assertThat(entity.getDeletedAt()).isBetween(before, after);
    }
}