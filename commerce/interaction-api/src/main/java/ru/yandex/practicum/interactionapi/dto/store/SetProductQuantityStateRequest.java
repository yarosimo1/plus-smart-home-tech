package ru.yandex.practicum.interactionapi.dto.store;

import jakarta.validation.constraints.NotNull;

public record SetProductQuantityStateRequest(
        @NotNull String productId,
        @NotNull QuantityState quantityState
) {
}