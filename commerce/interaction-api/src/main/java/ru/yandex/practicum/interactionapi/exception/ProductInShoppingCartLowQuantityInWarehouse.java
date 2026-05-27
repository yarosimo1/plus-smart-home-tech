package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

public class ProductInShoppingCartLowQuantityInWarehouse
        extends ApiException {

    public ProductInShoppingCartLowQuantityInWarehouse(
            String message
    ) {
        super(
                message,
                message,
                HttpStatus.BAD_REQUEST
        );
    }
}