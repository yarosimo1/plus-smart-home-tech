package ru.yandex.practicum.interactionapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;

    private final String userMessage;

    protected ApiException(
            String message,
            String userMessage,
            HttpStatus httpStatus
    ) {
        super(message);
        this.userMessage = userMessage;
        this.httpStatus = httpStatus;
    }
}
