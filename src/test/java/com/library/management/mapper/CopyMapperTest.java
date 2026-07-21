package com.library.management.mapper;

import com.library.management.dto.CopyInsertDTO;
import com.library.management.dto.CopyReadOnlyDTO;
import com.library.management.model.Book;
import com.library.management.model.Copy;
import com.library.management.model.CopyCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CopyMapperTest {

    private CopyMapper copyMapper;
    private Copy copy;
    private Book book;

    @BeforeEach
    void setUp() {
        copyMapper = new CopyMapper();

        book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));

        copy = new Copy();
        copy.setBook(book);
        copy.setAvailable(true);
        copy.setCondition(CopyCondition.NEW);
    }

    @Test
    void mapToCopyEntity_shouldMapCorrectly() {
        CopyInsertDTO dto = new CopyInsertDTO(book.getId(), true, CopyCondition.NEW);

        Copy mapped = copyMapper.mapToCopyEntity(dto);

        assertThat(mapped.getAvailable()).isTrue();
        assertThat(mapped.getCondition()).isEqualTo(CopyCondition.NEW);
    }

    @Test
    void mapToCopyReadOnlyDTO_shouldMapCorrectly() {
        CopyReadOnlyDTO dto = copyMapper.mapToCopyReadOnlyDTO(copy);

        assertThat(dto.available()).isTrue();
        assertThat(dto.condition()).isEqualTo(CopyCondition.NEW);
        assertThat(dto.bookTitle()).isEqualTo("Animal Farm");
        assertThat(dto.bookUuid()).isEqualTo(book.getId());
    }
}