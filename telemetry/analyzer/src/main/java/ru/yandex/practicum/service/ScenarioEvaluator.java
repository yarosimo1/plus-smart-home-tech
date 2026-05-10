package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.entity.ScenarioCondition;
import ru.yandex.practicum.entity.model.ConditionOperation;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Component
@RequiredArgsConstructor
public class ScenarioEvaluator {

    private final SensorStateExtractor extractor;

    public boolean matches(
            Scenario scenario,
            SensorsSnapshotAvro snapshot
    ) {

        return scenario.getConditions()
                .stream()
                .allMatch(condition ->
                        matchesCondition(
                                condition,
                                snapshot
                        )
                );
    }

    private boolean matchesCondition(
            ScenarioCondition condition,
            SensorsSnapshotAvro snapshot
    ) {

        SensorStateAvro state =
                snapshot.getSensorsState()
                        .get(condition.getSensorId());

        if (state == null) {
            return false;
        }

        Integer actual =
                extractor.extractValue(
                        state,
                        condition.getCondition().getType()
                );

        Integer expected =
                condition.getCondition().getValue();

        ConditionOperation operation =
                condition.getCondition().getOperation();

        return switch (operation) {

            case EQUALS -> actual.equals(expected);

            case GREATER_THAN -> actual > expected;

            case LOWER_THAN -> actual < expected;
        };
    }
}
