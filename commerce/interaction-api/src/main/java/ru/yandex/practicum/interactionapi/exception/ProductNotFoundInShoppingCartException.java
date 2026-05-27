package ru.yandex.practicum.interactionapi.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ProductNotFoundInShoppingCartException extends ApiException {

    public ProductNotFoundInShoppingCartException(UUID productId) {
        super(
                "Product not found in shopping cart: " + productId,
                "Товар отсутствует в корзине",
                HttpStatus.NOT_FOUND
        );
    }
}
