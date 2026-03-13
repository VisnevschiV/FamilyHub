package com.visnevschi.familyhub.config;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mongodb.MongoException;
import com.visnevschi.familyhub.dto.ApiError;
import com.visnevschi.familyhub.exception.InvalidCredentialsException;
import com.visnevschi.familyhub.exception.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.put(error.getField(), error.getDefaultMessage());
        }

        ApiError payload = buildError(HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                null,
                details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
                                                              HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                details.put(violation.getPropertyPath().toString(), violation.getMessage()));

        ApiError payload = buildError(HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                null,
                details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex,
                                                          HttpServletRequest request) {
        logger.warn("Bad request: {} {} - {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage());

        ApiError payload = buildError(HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI(),
                null,
                null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex,
                                                       HttpServletRequest request) {
        logger.warn("Conflict: {} {} - {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage());

        ApiError payload = buildError(HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI(),
                null,
                null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex,
                                                   HttpServletRequest request) {
        logger.warn("Not found: {} {} - {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage());

        ApiError payload = buildError(HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                null,
                null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
    }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex,
                                                                                                                         HttpServletRequest request) {
                logger.warn("Invalid credentials: {} {}",
                                request.getMethod(),
                                request.getRequestURI());

                ApiError payload = buildError(HttpStatus.UNAUTHORIZED,
                                ex.getMessage(),
                                request.getRequestURI(),
                                null,
                                null);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payload);
        }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex,
                                                     HttpServletRequest request) {
        logger.warn("Malformed request body: {} {}",
                request.getMethod(),
                request.getRequestURI());

        ApiError payload = buildError(HttpStatus.BAD_REQUEST,
                "Malformed JSON request",
                request.getRequestURI(),
                null,
                null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex,
                                                         HttpServletRequest request) {
        ApiError payload = buildError(HttpStatus.UNAUTHORIZED,
                "Authentication required",
                request.getRequestURI(),
                null,
                null);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payload);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex,
                                                        HttpServletRequest request) {
        ApiError payload = buildError(HttpStatus.FORBIDDEN,
                "Access denied",
                request.getRequestURI(),
                null,
                null);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(payload);
    }

        @ExceptionHandler({MongoException.class, DataAccessException.class})
        public ResponseEntity<ApiError> handleDatabaseUnavailable(Exception ex,
                                                                                                                          HttpServletRequest request) {
                String errorId = UUID.randomUUID().toString();
                logger.error("Database error {} on {} {}",
                                errorId,
                                request.getMethod(),
                                request.getRequestURI(),
                                ex);

                ApiError payload = buildError(HttpStatus.SERVICE_UNAVAILABLE,
                                "Database is temporarily unavailable. Reference: " + errorId,
                                request.getRequestURI(),
                                errorId,
                                null);

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(payload);
        }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex,
                                                           HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logger.error("Unhandled error {} on {} {}",
                errorId,
                request.getMethod(),
                request.getRequestURI(),
                ex);

        ApiError payload = buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error. Reference: " + errorId,
                request.getRequestURI(),
                errorId,
                null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload);
    }

    private ApiError buildError(HttpStatus status,
                                String message,
                                String path,
                                String errorId,
                                Map<String, String> details) {
        return new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                errorId,
                details
        );
    }
}

