package ru.yandex.practicum.interactionapi.dto.store;

import jakarta.validation.constraints.NotBlank;

public record ProductQuantityStateDto(
        @NotBlank String productId,
        @NotBlank String quantityState
) {
}

