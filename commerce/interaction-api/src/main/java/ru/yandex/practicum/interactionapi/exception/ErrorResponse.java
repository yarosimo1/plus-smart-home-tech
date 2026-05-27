package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        HttpStatus httpStatus,
        String message,
        String userMessage
) {
}
