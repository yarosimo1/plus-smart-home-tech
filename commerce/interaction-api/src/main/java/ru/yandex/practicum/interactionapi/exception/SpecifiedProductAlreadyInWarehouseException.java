package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

public class SpecifiedProductAlreadyInWarehouseException
        extends ApiException {

    public SpecifiedProductAlreadyInWarehouseException(
            String message
    ) {
        super(
                message,
                message,
                HttpStatus.BAD_REQUEST
        );
    }
}