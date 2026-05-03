package ru.yandex.practicum.service;

import ru.yandex.practicum.model.ConditionType;

public interface ValueExtractor {
    int extract(Object data);

    ConditionType getType();
}
