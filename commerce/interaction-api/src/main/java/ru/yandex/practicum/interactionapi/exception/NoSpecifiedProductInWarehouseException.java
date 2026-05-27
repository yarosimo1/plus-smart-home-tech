package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

public class NoSpecifiedProductInWarehouseException
        extends ApiException {

    public NoSpecifiedProductInWarehouseException(
            String message
    ) {
        super(
                message,
                message,
                HttpStatus.BAD_REQUEST
        );
    }
}