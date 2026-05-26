package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

public class ProductServiceUnavailableException extends ApiException {
    public ProductServiceUnavailableException(String message, String message2, HttpStatus status) {
        super(
                message,
                message2,
                status
        );
    }
}
