package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.Condition;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.entity.ScenarioAction;
import ru.yandex.practicum.entity.ScenarioCondition;
import ru.yandex.practicum.grpc.client.HubRouterClient;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SnapshotAnalyzer {

    private final ScenarioService scenarioService;
    private final HubRouterClient hubRouterClient;

    public void analyze(SensorsSnapshotAvro snapshot) {

        List<Scenario> scenarios =
                scenarioService.getScenarios(snapshot.getHubId());

        if (scenarios.isEmpty()) {
            log.debug(
                    "No scenarios found for hub={}",
                    snapshot.getHubId()
            );
            return;
        }

        for (Scenario scenario : scenarios) {

            boolean allConditionsMatched =
                    scenario.getConditions()
                            .stream()
                            .allMatch(condition ->
                                    checkCondition(condition, snapshot));

            if (!allConditionsMatched) {
                continue;
            }

            log.info(
                    "Scenario matched: hubId={}, scenario={}",
                    snapshot.getHubId(),
                    scenario.getName()
            );

            executeActions(snapshot, scenario);
        }
    }

    private boolean checkCondition(
            ScenarioCondition scenarioCondition,
            SensorsSnapshotAvro snapshot
    ) {

        SensorStateAvro sensorState =
                snapshot.getSensorsState()
                        .get(scenarioCondition.getSensorId());

        if (sensorState == null) {
            return false;
        }

        Condition condition = scenarioCondition.getCondition();

        if (condition == null) {
            return false;
        }

        Object data = sensorState.getData();

        return switch (condition.getType()) {

            case MOTION -> {

                if (data instanceof MotionSensorAvro motion) {

                    yield compareBoolean(
                            motion.getMotion(),
                            condition.getValue(),
                            ConditionOperationProto.valueOf(
                                    condition.getOperation().name()
                            )
                    );
                }

                yield false;
            }

            case SWITCH -> {

                if (data instanceof SwitchSensorAvro sw) {

                    yield compareBoolean(
                            sw.getState(),
                            condition.getValue(),
                            ConditionOperationProto.valueOf(
                                    condition.getOperation().name()
                            )
                    );
                }

                yield false;
            }

            case TEMPERATURE -> {

                Integer temperature = extractTemperature(data);

                if (temperature == null) {
                    yield false;
                }

                yield compareInteger(
                        temperature,
                        condition.getValue(),
                        ConditionOperationProto.valueOf(
                                condition.getOperation().name()
                        )
                );
            }

            case HUMIDITY -> {

                if (data instanceof ClimateSensorAvro climate) {

                    yield compareInteger(
                            climate.getHumidity(),
                            condition.getValue(),
                            ConditionOperationProto.valueOf(
                                    condition.getOperation().name()
                            )
                    );
                }

                yield false;
            }

            case CO2LEVEL -> {

                if (data instanceof ClimateSensorAvro climate) {

                    yield compareInteger(
                            climate.getCo2Level(),
                            condition.getValue(),
                            ConditionOperationProto.valueOf(
                                    condition.getOperation().name()
                            )
                    );
                }

                yield false;
            }

            case LUMINOSITY -> {

                if (data instanceof LightSensorAvro light) {

                    yield compareInteger(
                            light.getLuminosity(),
                            condition.getValue(),
                            ConditionOperationProto.valueOf(
                                    condition.getOperation().name()
                            )
                    );
                }

                yield false;
            }
        };
    }

    private Integer extractTemperature(Object data) {

        if (data instanceof ClimateSensorAvro climate) {
            return climate.getTemperatureC();
        }

        if (data instanceof TemperatureSensorAvro temperature) {
            return temperature.getTemperatureC();
        }

        return null;
    }

    /**
     * В БД boolean хранится как Integer:
     * 1 = true
     * 0 = false
     */
    private boolean compareBoolean(
            boolean actual,
            Integer expected,
            ConditionOperationProto operation
    ) {

        if (expected == null) {
            return false;
        }

        if (operation != ConditionOperationProto.EQUALS) {
            return false;
        }

        boolean expectedBoolean = expected == 1;

        return actual == expectedBoolean;
    }

    private boolean compareInteger(
            int actual,
            Integer expected,
            ConditionOperationProto operation
    ) {

        if (expected == null) {
            return false;
        }

        return switch (operation) {
            case EQUALS -> actual == expected;
            case GREATER_THAN -> actual > expected;
            case LOWER_THAN -> actual < expected;
            default -> false;
        };
    }

    private void executeActions(
            SensorsSnapshotAvro snapshot,
            Scenario scenario
    ) {

        for (ScenarioAction scenarioAction : scenario.getActions()) {

            if (scenarioAction.getAction() == null) {
                continue;
            }

            DeviceActionProto.Builder actionBuilder =
                    DeviceActionProto.newBuilder()
                            .setSensorId(scenarioAction.getSensorId())
                            .setType(
                                    ActionTypeProto.valueOf(
                                            scenarioAction.getAction()
                                                    .getType()
                                                    .name()
                                    )
                            );

            if (scenarioAction.getAction().getValue() != null) {
                actionBuilder.setValue(
                        scenarioAction.getAction().getValue()
                );
            }

            DeviceActionRequest request =
                    DeviceActionRequest.newBuilder()
                            .setHubId(snapshot.getHubId())
                            .setScenarioName(scenario.getName())
                            .setAction(actionBuilder.build())
                            .setTimestamp(
                                    toProtoTimestamp(Instant.now())
                            )
                            .build();

            log.info(
                    "Executing action: hubId={}, scenario={}, sensorId={}, action={}",
                    snapshot.getHubId(),
                    scenario.getName(),
                    scenarioAction.getSensorId(),
                    scenarioAction.getAction().getType().name()
            );

            hubRouterClient.handleDeviceAction(request);
        }
    }

    private com.google.protobuf.Timestamp toProtoTimestamp(
            Instant instant
    ) {

        return com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}