package com.library.management.core;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.ErrorResponseDTO;
import com.library.management.dto.ValidationErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Translates exceptions into HTTP responses, so that no controller has to catch anything.
 *
 * <p>Spring picks the handler whose declared type is closest to the thrown exception, which is why
 * the catch-all {@code Exception} handler does not swallow the specific ones. It is last in the
 * file only for readability.
 *
 * <p>Exceptions that Spring MVC raises before a controller is reached — a malformed body, a failed
 * {@code @Valid} check — belong to {@link ResponseEntityExceptionHandler} and are customised by
 * overriding its methods. Adding an {@code @ExceptionHandler} for the same type instead of
 * overriding makes the two ambiguous and the application fails to start.
 */
@ControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn("Entity not found. Message={}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        log.warn("Entity already exists. Message={}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(EntityInvalidArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityInvalidArgumentException(EntityInvalidArgumentException e) {
        log.warn("Invalid argument. Message={}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(e.getCode(), e.getMessage()));

    }

    /**
     * The response deliberately does not say whether it was the username or the password that was
     * wrong, so that the endpoint cannot be used to discover which accounts exist. The IP is logged
     * instead, where repeated attempts are visible.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(
            AuthenticationException e,
            HttpServletRequest request) {

        log.warn("Failed login attempt from IP={}", request.getRemoteAddr());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO("BAD_CREDENTIALS", "Invalid username or password"));
    }

    /**
     * Raised when {@code ?sort=} names a property the entity does not have. It is the client's
     * mistake, not the server's, so it answers 400 rather than the 500 it would default to.
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponseDTO> handlePropertyReferenceException(PropertyReferenceException e) {
        log.warn("Invalid sort or filter property. Message={}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO("INVALID_PROPERTY", e.getMessage()));
    }

    /**
     * Replaces the default RFC 7807 body with a map of field to message, so the client
     * can show each error next to the input it belongs to.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("Validation failed for fields={}", errors.keySet());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponseDTO(
                        "VALIDATION_ERROR",
                        "The request body is not valid",
                        errors));
    }

    /** The real message goes to the log, not to the client: it names tables and constraints. */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataAccessException(DataAccessException e) {
        log.error("Database error. Message={}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("DATABASE_ERROR", "A database error occurred."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception e) {
        log.warn("Unexpected error. Message={}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("INTERNAL_SERVER_ERROR", "An unexpected error occurred."));
    }
}