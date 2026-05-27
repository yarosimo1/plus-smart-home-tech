package ru.yandex.practicum.interactionapi.dto.store;

import java.util.List;

public record PageProductDto(
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        int size,
        List<ProductDto> content,
        int number,
        List<SortObject> sort,
        int numberOfElements,
        PageableObject pageable,
        boolean empty
) {
}

