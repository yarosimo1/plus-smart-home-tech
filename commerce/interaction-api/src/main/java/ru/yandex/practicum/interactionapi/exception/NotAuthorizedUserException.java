package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

public class NotAuthorizedUserException extends ApiException {

    public NotAuthorizedUserException(String userName) {
        super(
                "User is not authorized: " + userName,
                "Пользователь не авторизован",
                HttpStatus.UNAUTHORIZED
        );
    }
}