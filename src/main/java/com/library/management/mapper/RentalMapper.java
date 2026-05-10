package com.library.management.mapper;

import com.library.management.dto.RentalInsertDTO;
import com.library.management.dto.RentalReadOnlyDTO;
import com.library.management.model.Rental;
import org.springframework.stereotype.Component;

@Component
public class RentalMapper {

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
