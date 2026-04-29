package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.ScenarioCondition;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConditionEvaluator {

    private final Map<String, ValueExtractor> extractors;

    public boolean evaluate(SensorsSnapshotAvro snapshot,
                            ScenarioCondition sc) {

        String sensorId = sc.getSensor().getId();

        SensorStateAvro state = snapshot.getSensorsState().get(sensorId);

        if (state == null) {
            return false;
        }

        String type = sc.getCondition().getType();

        ValueExtractor extractor = extractors.get(type);

        if (extractor == null) {
            return false;
        }

        int actual = extractor.extract(state.getData());
        int expected = (int) sc.getCondition().getValue();

        return switch (sc.getCondition().getOperation()) {
            case "GREATER_THAN" -> actual > expected;
            case "LESS_THAN" -> actual < expected;
            case "EQUAL" -> actual == expected;
            default -> false;
        };
    }
}