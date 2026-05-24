package ru.yandex.practicum.shoppingcart.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        HttpStatus httpStatus,
        String message,
        String userMessage
) {
}
