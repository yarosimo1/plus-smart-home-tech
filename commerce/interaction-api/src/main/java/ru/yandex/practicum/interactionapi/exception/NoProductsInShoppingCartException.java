package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

public class NoProductsInShoppingCartException extends ApiException {

    public NoProductsInShoppingCartException() {
        super(
                "Shopping cart is empty",
                "В корзине нет товаров",
                HttpStatus.BAD_REQUEST
        );
    }
}