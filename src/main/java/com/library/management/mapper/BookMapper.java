package com.library.management.mapper;

import com.library.management.dto.AuthorReadOnlyDTO;
import com.library.management.dto.BookInsertDTO;
import com.library.management.dto.BookReadOnlyDTO;
import com.library.management.model.Book;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public Book mapToBookEntity(BookInsertDTO insertDTO) {
        Book book = new Book();

        book.setIsbn(insertDTO.isbn());
        book.setTitle(insertDTO.title());
        book.setDescription(insertDTO.description());
        book.setLanguage(insertDTO.language());
        book.setDailyCost(insertDTO.dailyCost());
        book.setPublishedDate(insertDTO.publishedDate());

        return book;
    }

    public BookReadOnlyDTO mapToBookReadOnlyDTO(Book book) {

        Set<AuthorReadOnlyDTO> authors = book.getAllAuthors().stream()
                .map(author -> new AuthorReadOnlyDTO(
                        author.getId(),
                        author.getFirstname(),
                        author.getLastname(),
                        author.getBirthDate(),
                        author.getBirthPlace(),
                        author.getBio(),
                        null
                ))
                .collect(Collectors.toSet());

        return new BookReadOnlyDTO(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedDate(),
                book.getLanguage(),
                book.getDailyCost(),
                book.getDescription(),
                authors
        );
    }
}
