package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

public class ShoppingCartDeactivatedException extends ApiException {

    public ShoppingCartDeactivatedException() {
        super(
                "Shopping cart is deactivated",
                "Корзина деактивирована",
                HttpStatus.BAD_REQUEST
        );
    }
}