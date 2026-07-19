package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.BookInsertDTO;
import com.library.management.dto.BookUpdateDTO;
import com.library.management.model.*;
import com.library.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class BookServiceTest {

    @Autowired
    private IBookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CopyRepository copyRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Book existingBook;
    private Author existingAuthor;

    @BeforeEach
    void setUp() {
        existingAuthor = new Author();
        existingAuthor.setFirstname("George");
        existingAuthor.setLastname("Orwell");
        existingAuthor.setBirthDate(LocalDate.of(1903, 6, 25));
        authorRepository.save(existingAuthor);

        existingBook = new Book();
        existingBook.setTitle("Animal Farm");
        existingBook.setIsbn("978-0-452-28424-4");
        existingBook.setLanguage("English");
        existingBook.setDailyCost(BigDecimal.valueOf(1.20));
        existingBook.setPublishedDate(LocalDate.of(1945, 8, 17));
        bookRepository.save(existingBook);
    }

    @Test
    void saveBook_whenValidData_shouldSaveAndReturnBook()
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        BookInsertDTO dto = new BookInsertDTO(
                "1984", "978-0-452-28423-4", LocalDate.of(1949, 6, 8),
                "English", BigDecimal.valueOf(1.50), "Dystopian novel",
                Set.of(existingAuthor.getId())
        );

        Book saved = bookService.saveBook(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("1984");
        assertThat(saved.getIsbn()).isEqualTo("978-0-452-28423-4");
    }

    @Test
    void saveBook_whenIsbnAlreadyExists_shouldThrowException() {
        BookInsertDTO dto = new BookInsertDTO(
                "Animal Farm", "978-0-452-28424-4", LocalDate.of(1945, 8, 17),
                "English", BigDecimal.valueOf(1.20), "A political allegory", null
        );

        assertThatThrownBy(() -> bookService.saveBook(dto))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    void saveBook_whenDailyCostNegative_shouldThrowException() {
        BookInsertDTO dto = new BookInsertDTO(
                "1984", "978-0-452-28423-4", LocalDate.of(1949, 6, 8),
                "English", BigDecimal.valueOf(-1.00), "Dystopian novel", null
        );

        assertThatThrownBy(() -> bookService.saveBook(dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void saveBook_whenPublishedDateInFuture_shouldThrowException() {
        BookInsertDTO dto = new BookInsertDTO(
                "1984", "978-0-452-28423-4", LocalDate.now().plusDays(1),
                "English", BigDecimal.valueOf(1.50), "Dystopian novel", null
        );

        assertThatThrownBy(() -> bookService.saveBook(dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void saveBook_whenAuthorNotFound_shouldThrowException() {
        BookInsertDTO dto = new BookInsertDTO(
                "1984", "978-0-452-28423-4", LocalDate.of(1949, 6, 8),
                "English", BigDecimal.valueOf(1.50), "Dystopian novel",
                Set.of(UUID.randomUUID())
        );

        assertThatThrownBy(() -> bookService.saveBook(dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateBook_whenValidData_shouldUpdateAndReturn()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        BookUpdateDTO dto = new BookUpdateDTO("Greek", BigDecimal.valueOf(2.00), "Updated description");

        Book updated = bookService.updateBook(existingBook.getId(), dto);

        assertThat(updated).isNotNull();
        assertThat(updated.getLanguage()).isEqualTo("Greek");
        assertThat(updated.getDailyCost()).isEqualTo(BigDecimal.valueOf(2.00));
    }

    @Test
    void updateBook_whenNotFound_shouldThrowException() {
        BookUpdateDTO dto = new BookUpdateDTO("Greek", BigDecimal.valueOf(2.00), "Updated description");

        assertThatThrownBy(() -> bookService.updateBook(UUID.randomUUID(), dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateBook_whenDailyCostNegative_shouldThrowException() {
        BookUpdateDTO dto = new BookUpdateDTO("Greek", BigDecimal.valueOf(-1.00), "Updated description");

        assertThatThrownBy(() -> bookService.updateBook(existingBook.getId(), dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void deleteBookByUuid_whenNoActiveRentals_shouldSoftDelete()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        bookService.deleteBookByUuid(existingBook.getId());

        Book deleted = bookRepository.findById(existingBook.getId()).orElseThrow();
        assertThat(deleted.isDeleted()).isTrue();
    }

    @Test
    void deleteBookByUuid_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> bookService.deleteBookByUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteBookByUuid_whenHasActiveRentals_shouldThrowException() {
        Copy copy = new Copy();
        copy.setBook(existingBook);
        copy.setAvailable(false);
        copy.setCondition(CopyCondition.NEW);
        copyRepository.save(copy);
        existingBook.addCopy(copy);  // ← προσθήκη
        bookRepository.save(existingBook);  // ← προσθήκη

        Address address = new Address();
        address.setStreet("Ermou");
        address.setStreetNumber("10");
        address.setCity("Athens");
        address.setCountry("Greece");
        address.setPostalCode("10563");

        Member member = new Member();
        member.setFirstname("Thanos");
        member.setLastname("Pagkratis");
        member.setEmail("thanos@example.com");
        member.setPhoneNumber("6912345678");
        member.setBirthDate(LocalDate.of(1990, 1, 1));
        member.setMembershipDate(LocalDate.of(2024, 1, 1));
        member.setAddress(address);
        memberRepository.save(member);

        Rental rental = new Rental();
        rental.setMember(member);
        rental.setCopy(copy);
        rental.setRentalDate(Instant.now());
        rental.setDueDate(Instant.now().plusSeconds(86400));
        rentalRepository.save(rental);
        copy.addRental(rental);  // ← προσθήκη
        copyRepository.save(copy);  // ← προσθήκη

        assertThatThrownBy(() -> bookService.deleteBookByUuid(existingBook.getId()))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void getBookByUuidDeletedFalse_whenExists_shouldReturnBook() throws EntityNotFoundException {
        Book found = bookService.getBookByUuidDeletedFalse(existingBook.getId());
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Animal Farm");
    }

    @Test
    void getBookByUuidDeletedFalse_whenDeleted_shouldThrowException() {
        existingBook.softDelete();
        bookRepository.save(existingBook);

        assertThatThrownBy(() -> bookService.getBookByUuidDeletedFalse(existingBook.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getBookByUuidDeletedFalse_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> bookService.getBookByUuidDeletedFalse(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getBooksPaginatedAndDeletedFalse_shouldReturnOnlyActiveBooks() {
        existingBook.softDelete();
        bookRepository.save(existingBook);

        Book activeBook = new Book();
        activeBook.setTitle("1984");
        activeBook.setIsbn("978-0-452-28423-4");
        activeBook.setLanguage("English");
        activeBook.setDailyCost(BigDecimal.valueOf(1.50));
        bookRepository.save(activeBook);

        Page<Book> books = bookService.getBooksPaginatedAndDeletedFalse(PageRequest.of(0, 10));
        assertThat(books.getContent()).hasSize(1);
        assertThat(books.getContent().get(0).getTitle()).isEqualTo("1984");
    }

    @Test
    void isBookExistByIsbn_whenExists_shouldReturnTrue() {
        boolean exists = bookService.isBookExistByIsbn("978-0-452-28424-4");
        assertThat(exists).isTrue();
    }

    @Test
    void isBookExistByIsbn_whenNotExists_shouldReturnFalse() {
        boolean exists = bookService.isBookExistByIsbn("000-0-000-00000-0");
        assertThat(exists).isFalse();
    }
}