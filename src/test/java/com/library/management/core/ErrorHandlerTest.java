package com.library.management.core;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleEntityNotFoundException_shouldReturn404() {
        EntityNotFoundException ex = new EntityNotFoundException("Author", "Author not found");

        ResponseEntity<ErrorResponseDTO> response = errorHandler.handleEntityNotFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().description()).isEqualTo("Author not found");
    }

    @Test
    void handleEntityAlreadyExistsException_shouldReturn409() {
        EntityAlreadyExistsException ex = new EntityAlreadyExistsException("Author", "Author already exists");

        ResponseEntity<ErrorResponseDTO> response = errorHandler.handleEntityAlreadyExistsException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().description()).isEqualTo("Author already exists");
    }

    @Test
    void handleEntityInvalidArgumentException_shouldReturn400() {
        EntityInvalidArgumentException ex = new EntityInvalidArgumentException("Author", "Invalid argument");

        ResponseEntity<ErrorResponseDTO> response = errorHandler.handleEntityInvalidArgumentException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().description()).isEqualTo("Invalid argument");
    }

    @Test
    void handleGenericException_shouldReturn500() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ErrorResponseDTO> response = errorHandler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_SERVER_ERROR");
    }
}