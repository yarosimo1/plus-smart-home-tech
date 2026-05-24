package ru.yandex.practicum.interactionapi.dto.store;

public record SetProductQuantityStateRequest(
        String productId,
        QuantityState quantityState
) {
}