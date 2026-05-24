package ru.yandex.practicum.interactionapi.dto.store;

public record SortObject(
        String direction,
        String nullHandling,
        boolean ascending,
        String property,
        boolean ignoreCase
) {
}

