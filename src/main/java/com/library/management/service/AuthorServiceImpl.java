package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.AuthorInsertDTO;
import com.library.management.dto.AuthorReadOnlyDTO;
import com.library.management.dto.AuthorUpdateDTO;
import com.library.management.mapper.AuthorMapper;
import com.library.management.model.Author;
import com.library.management.repository.AuthorRepository;
import com.library.management.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements IAuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper mapper;

    @Override
    @Transactional(rollbackFor = EntityInvalidArgumentException.class)
    public AuthorReadOnlyDTO saveAuthor(AuthorInsertDTO authorInsertDTO)
            throws EntityInvalidArgumentException {

        try {
            if (authorInsertDTO.birthDate() != null && authorInsertDTO.birthDate().isAfter(LocalDate.now())) {
                throw new EntityInvalidArgumentException("Author", "Birth date cannot be in the future");
            }

            Author author = mapper.mapToAuthorEntity(authorInsertDTO);

            Author savedAuthor = authorRepository.save(author);
            log.info("Author saved with uuid={}", savedAuthor.getId());

            return mapper.mapToAuthorReadOnlyDTO(savedAuthor);
        } catch (EntityInvalidArgumentException e) {
            log.error("Save author failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = EntityNotFoundException.class)
    public AuthorReadOnlyDTO updateAuthor(UUID id, AuthorUpdateDTO authorUpdateDTO)
            throws EntityNotFoundException {

        try {
            Author author = authorRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Author", "Author with uuid= " + id + " not found."));

            author.setFirstname(authorUpdateDTO.firstname());
            author.setLastname(authorUpdateDTO.lastname());
            author.setBio(authorUpdateDTO.bio());
            author.setBirthDate(authorUpdateDTO.birthDate());
            author.setBirthPlace(authorUpdateDTO.birthPlace());

            Author updatedAuthor = authorRepository.save(author);
            log.info("Author with uuid={} has successfully been updated", updatedAuthor.getId());

            return mapper.mapToAuthorReadOnlyDTO(updatedAuthor);
        } catch (EntityNotFoundException e) {
            log.error("Update author with uuid={} failed. {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public void deleteAuthorByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException {

        try {
            Author author = authorRepository.findById(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Author", "Author with uuid= " + uuid + " not found."));

            boolean hasBookWithSingleAuthor = author.getAllBooks().stream()
                    .anyMatch(book -> book.getAllAuthors().size() == 1);

            if (hasBookWithSingleAuthor) {
                throw new EntityInvalidArgumentException("Author",
                        "Cannot delete author — some books have only this author");
            }

            author.softDelete();
            authorRepository.save(author);
            log.info("Author with uuid={} has successfully been deleted", uuid);

        } catch (EntityNotFoundException | EntityInvalidArgumentException e) {
            log.error("Delete author failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorReadOnlyDTO getAuthorByUuid(UUID uuid) throws EntityNotFoundException {

        try {
            Author author = authorRepository.findById(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Author", "Author with uuid= " + uuid + " not found."));

            log.info("Get author by uuid={} returned successfully", uuid);

            return mapper.mapToAuthorReadOnlyDTO(author);
        } catch (EntityNotFoundException e) {
            log.error("Get author by id failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorReadOnlyDTO getAuthorByUUIDDeletedFalse(UUID uuid) throws EntityNotFoundException {

        try {
            Author author = authorRepository.findByIdAndDeletedFalse(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Author", "Author with uuid= " + uuid + " not found."));

            log.info("Get author by uuid={} and deleted false returned successfully", uuid);

            return mapper.mapToAuthorReadOnlyDTO(author);
        } catch (EntityNotFoundException e) {
            log.error("Get author by uuid and deleted false failed. {}", e.getMessage());
            throw e;
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Page<AuthorReadOnlyDTO> getAuthorsPaginated(Pageable pageable) {

        Page<Author> authorsPage = authorRepository.findAll(pageable);
        log.info("Get authors paginated returned successfully page={} and size={}", authorsPage.getNumber(), authorsPage.getSize());
        return authorsPage.map(mapper::mapToAuthorReadOnlyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthorReadOnlyDTO> getAuthorsPaginatedAndDeletedFalse(Pageable pageable) {

        Page<Author> authorsPage = authorRepository.findByDeletedFalse(pageable);
        log.info("Get authors paginated and deleted false returned successfully page={} and size={}", authorsPage.getNumber(), authorsPage.getSize());
        return authorsPage.map(mapper::mapToAuthorReadOnlyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAuthorExistByLastname(String lastname) {

        return authorRepository.existsByLastname(lastname);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorReadOnlyDTO> getAuthorsByBookUuid(UUID bookUuid) throws EntityNotFoundException {

        try {

            if (!bookRepository.existsById(bookUuid)) {
                throw new EntityNotFoundException("Book", "Book with uuid=" + bookUuid + " not found");
            }

            List<Author> authors = authorRepository.findByBooks_Id(bookUuid);

            log.info("Get authors by bookUuid={} returned successfully", bookUuid);

            return authors.stream()
                    .map(mapper::mapToAuthorReadOnlyDTO)
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            log.error("Get authors by bookUuid={} failed. {}", bookUuid, e.getMessage());
            throw e;
        }

    }


}
