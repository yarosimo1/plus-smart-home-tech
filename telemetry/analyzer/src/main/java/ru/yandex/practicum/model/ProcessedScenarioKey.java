package ru.yandex.practicum.model;

public record ProcessedScenarioKey(
        String hubId,
        String scenarioName,
        long timestamp
) {
}
