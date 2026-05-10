package ru.yandex.practicum.scenario;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.Condition;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.entity.ScenarioAction;
import ru.yandex.practicum.entity.ScenarioCondition;
import ru.yandex.practicum.grpc.ActionExecutor;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.ProcessedScenarioKey;
import ru.yandex.practicum.service.ScenarioService;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioEngine {
    private final Set<ProcessedScenarioKey> processed =
            ConcurrentHashMap.newKeySet();

    private final ScenarioService scenarioService;

    private final ActionExecutor actionExecutor;

    public void processSnapshot(SensorsSnapshotAvro snapshot) {

        List<Scenario> scenarios =
                scenarioService.getScenarios(
                        snapshot.getHubId().toString()
                );

        for (Scenario scenario : scenarios) {

            if (checkScenario(snapshot, scenario)) {
                ProcessedScenarioKey key =
                        new ProcessedScenarioKey(
                                snapshot.getHubId().toString(),
                                scenario.getName(),
                                snapshot.getTimestamp().toEpochMilli()
                        );

                if (!processed.add(key)) {
                    return;
                }

                executeScenario(snapshot, scenario);
            }
        }
    }

    private boolean checkScenario(
            SensorsSnapshotAvro snapshot,
            Scenario scenario
    ) {

        return scenario.getConditions().stream()
                .allMatch(condition ->
                        checkCondition(snapshot, condition)
                );
    }

    private boolean checkCondition(
            SensorsSnapshotAvro snapshot,
            ScenarioCondition conditionLink
    ) {

        SensorStateAvro state =
                snapshot.getSensorsState()
                        .get(conditionLink.getSensorId());

        if (state == null) {
            return false;
        }

        Condition condition = conditionLink.getCondition();

        Integer sensorValue = extractValue(
                state.getData(),
                condition.getType().name()
        );

        if (sensorValue == null) {
            return false;
        }

        int expected = condition.getValue();

        return switch (condition.getOperation()) {

            case EQUALS -> sensorValue.equals(expected);

            case GREATER_THAN -> sensorValue > expected;

            case LOWER_THAN -> sensorValue < expected;

            default -> false;
        };
    }

    private Integer extractValue(
            Object data,
            String type
    ) {

        return switch (type) {

            case "TEMPERATURE" -> {
                if (data instanceof ClimateSensorAvro c) {
                    yield c.getTemperatureC();
                }

                if (data instanceof TemperatureSensorAvro t) {
                    yield t.getTemperatureC();
                }

                yield null;
            }

            case "MOTION" -> {
                if (data instanceof MotionSensorAvro m) {
                    yield m.getMotion() ? 1 : 0;
                }

                yield null;
            }

            case "LUMINOSITY" -> {
                if (data instanceof LightSensorAvro l) {
                    yield l.getLuminosity();
                }

                yield null;
            }

            case "HUMIDITY" -> {
                if (data instanceof ClimateSensorAvro c) {
                    yield c.getHumidity();
                }

                yield null;
            }

            case "CO2LEVEL" -> {
                if (data instanceof ClimateSensorAvro c) {
                    yield c.getCo2Level();
                }

                yield null;
            }

            case "SWITCH" -> {
                if (data instanceof SwitchSensorAvro s) {
                    yield s.getState() ? 1 : 0;
                }

                yield null;
            }

            default -> null;
        };
    }

    private void executeScenario(
            SensorsSnapshotAvro snapshot,
            Scenario scenario
    ) {

        for (ScenarioAction actionLink : scenario.getActions()) {

            actionExecutor.execute(
                    snapshot.getHubId().toString(),
                    scenario.getName(),
                    actionLink.getSensorId(),
                    actionLink.getAction()
            );
        }
    }
}
