package ru.yandex.practicum.shoppingcart.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interactionapi.exception.ApiException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        log.warn("Business exception: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                ex.getHttpStatus(),
                ex.getMessage(),
                ex.getUserMessage()
        );

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField()
                                + ": "
                                + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", message);

        ErrorResponse response = new ErrorResponse(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                message,
                "Ошибка валидации запроса"
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse>
    handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "Ошибка в параметрах запроса"
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unexpected error", ex);

        ErrorResponse response = new ErrorResponse(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                "Внутренняя ошибка сервера"
        );

        return ResponseEntity
                .internalServerError()
                .body(response);
    }
}
