package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

public class WarehouseUnavailableException extends ApiException {
    public WarehouseUnavailableException(
            String message,
            String userMessage,
            HttpStatus httpStatus
    ) {
        super(message, userMessage, httpStatus);
    }
}
