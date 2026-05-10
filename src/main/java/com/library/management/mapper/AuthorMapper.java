package com.library.management.mapper;

import com.library.management.dto.AuthorInsertDTO;
import com.library.management.dto.AuthorReadOnlyDTO;
import com.library.management.dto.BookReadOnlyDTO;
import com.library.management.model.Author;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthorMapper {

    public Author mapToAuthorEntity(AuthorInsertDTO insertDTO) {
        Author author = new Author();
        author.setFirstname(insertDTO.firstname());
        author.setLastname(insertDTO.lastname());
        author.setBio(insertDTO.bio());
        author.setBirthDate(insertDTO.birthDate());
        author.setBirthPlace(insertDTO.birthPlace());
        return author;
    }

    public AuthorReadOnlyDTO mapToAuthorReadOnlyDTO(Author author) {
        Set<BookReadOnlyDTO> books = author.getAllBooks().stream()
                .map(book -> new BookReadOnlyDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getPublishedDate(),
                        book.getLanguage(),
                        book.getDailyCost(),
                        book.getDescription(),
                        null
                ))
                .collect(Collectors.toSet());

        return new AuthorReadOnlyDTO(
                author.getId(),
                author.getFirstname(),
                author.getLastname(),
                author.getBirthDate(),
                author.getBirthPlace(),
                author.getBio(),
                books
        );
    }
}