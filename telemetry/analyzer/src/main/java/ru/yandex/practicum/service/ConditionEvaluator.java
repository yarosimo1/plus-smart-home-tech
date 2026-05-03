package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionType;
import ru.yandex.practicum.model.ScenarioCondition;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConditionEvaluator {
    private final Map<ConditionType, ValueExtractor> extractors;

    public ConditionEvaluator(List<ValueExtractor> list) {
        this.extractors = list.stream()
                .collect(Collectors.toMap(ValueExtractor::getType, Function.identity()));
    }

    public boolean evaluate(SensorsSnapshotAvro snapshot,
                            ScenarioCondition sc) {

        String sensorId = sc.getSensor().getId();
        SensorStateAvro state = snapshot.getSensorsState().get(sensorId);

        if (state == null) {
            return false;
        }

        Condition condition = sc.getCondition();

        ValueExtractor extractor = extractors.get(condition.getType());

        if (extractor == null) {
            return false;
        }

        int actual = extractor.extract(state.getData());
        int expected = condition.getValue();

        return switch (condition.getOperation()) {
            case GREATER_THAN -> actual > expected;
            case LOWER_THAN -> actual < expected;
            case EQUALS -> actual == expected;
        };
    }
}