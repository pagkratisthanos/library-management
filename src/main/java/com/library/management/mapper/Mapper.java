package com.library.management.mapper;

import com.library.management.dto.*;
import com.library.management.model.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Mapper {

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

    public Member mapToMemberEntity(MemberInsertDTO insertDTO) {

        Address address = new Address();

        address.setCountry(insertDTO.addressInsertDTO().country());
        address.setCity(insertDTO.addressInsertDTO().city());
        address.setStreet(insertDTO.addressInsertDTO().street());
        address.setStreetNumber(insertDTO.addressInsertDTO().streetNumber());
        address.setPostalCode(insertDTO.addressInsertDTO().postalCode());

        Member member = new Member();

        member.setFirstname(insertDTO.firstname());
        member.setLastname(insertDTO.lastname());
        member.setBirthDate(insertDTO.birthDate());
        member.setEmail(insertDTO.email());
        member.setPhoneNumber(insertDTO.phoneNumber());
        member.setMembershipDate(insertDTO.membershipDate());
        member.setAddress(address);

        return member;
    }

    public MemberReadOnlyDTO mapToMemberReadOnlyDTO(Member member) {

        AddressReadOnlyDTO addressReadOnlyDTO = new AddressReadOnlyDTO(
                member.getAddress().getId(),
                member.getAddress().getStreet(),
                member.getAddress().getStreetNumber(),
                member.getAddress().getCity(),
                member.getAddress().getCountry(),
                member.getAddress().getPostalCode()
        );

        return new MemberReadOnlyDTO(
                member.getId(),
                addressReadOnlyDTO,
                member.getFirstname(),
                member.getLastname(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getMembershipDate(),
                member.getBirthDate()
        );
    }

    public Copy mapToCopyEntity(CopyInsertDTO copyInsertDTO) {

        Copy copy = new Copy();

        copy.setAvailable(copyInsertDTO.available());
        copy.setCondition(copyInsertDTO.condition());

        return copy;
    }

    public CopyReadOnlyDTO mapToCopyReadOnlyDTO(Copy copy) {

        return new CopyReadOnlyDTO(
                copy.getId(),
                copy.getBook().getId(),
                copy.getBook().getTitle(),
                copy.getAvailable(),
                copy.getCondition()
        );
    }

    public Rental mapToRentalEntity(RentalInsertDTO rentalInsertDTO) {

        Rental rental = new Rental();

        rental.setDueDate(rentalInsertDTO.dueDate());

        return rental;
    }

    public RentalReadOnlyDTO mapToRentalReadOnlyDTO(Rental rental) {

        return new RentalReadOnlyDTO(
                rental.getId(),
                rental.getMember().getId(),
                rental.getCopy().getId(),
                rental.getRentalDate(),
                rental.getDueDate(),
                rental.getReturnDate(),
                rental.getMember().getFirstname(),
                rental.getMember().getLastname(),
                rental.getCopy().getBook().getTitle()
        );
    }
}
