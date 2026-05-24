package ru.yandex.practicum.interactionapi.dto.store;

import java.util.List;

public record PageableObject(
        long offset,
        List<SortObject> sort,
        boolean unpaged,
        boolean paged,
        int pageNumber,
        int pageSize
) {
}