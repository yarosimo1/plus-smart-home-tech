package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.*;
import ru.yandex.practicum.entity.model.ActionType;
import ru.yandex.practicum.entity.model.ConditionOperation;
import ru.yandex.practicum.entity.model.ConditionType;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScenarioService {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Transactional
    public void addSensor(DeviceAddedEventAvro event, String hubId) {
        String sensorId = event.getId().toString();

        if (sensorRepository.existsById(sensorId)) {
            return;
        }

        Sensor sensor = Sensor.builder()
                .id(sensorId)
                .hubId(hubId)
                .build();

        sensorRepository.save(sensor);
    }

    @Transactional
    public void removeSensor(DeviceRemovedEventAvro event) {
        sensorRepository.deleteById(
                event.getId().toString()
        );
    }

    @Transactional
    public void addScenario(
            ScenarioAddedEventAvro event,
            String hubId
    ) {

        Scenario scenario = scenarioRepository
                .findByHubIdAndName(
                        hubId,
                        event.getName().toString()
                )
                .orElseGet(() ->
                        Scenario.builder()
                                .hubId(hubId)
                                .name(event.getName().toString())
                                .build()
                );

        scenario = scenarioRepository.save(scenario);

        scenario.getConditions().clear();
        scenario.getActions().clear();

        /*
         * Загружаем все sensor одним запросом
         */
        Set<String> sensorIds = collectSensorIds(event);

        Map<String, Sensor> sensors = sensorRepository
                .findAllByIdIn(sensorIds)
                .stream()
                .collect(Collectors.toMap(
                        Sensor::getId,
                        Function.identity()
                ));

        /*
         * CONDITIONS
         */
        for (ScenarioConditionAvro conditionAvro : event.getConditions()) {
            String sensorId =
                    conditionAvro.getSensorId().toString();

            Sensor sensor = getSensorOrThrow(
                    sensors,
                    sensorId
            );

            Condition condition = Condition.builder()
                    .type(ConditionType.valueOf(
                            conditionAvro.getConditionType().name()))
                    .operation(ConditionOperation.valueOf(
                            conditionAvro.getOperation().name()))
                    .value(extractConditionValue(conditionAvro))
                    .build();

            condition = conditionRepository.save(condition);

            ScenarioCondition scenarioCondition =
                    ScenarioCondition.builder()
                            .id(
                                    new ScenarioConditionId(
                                            scenario.getId(),
                                            sensor.getId(),
                                            condition.getId()
                                    )
                            )
                            .scenario(scenario)
                            .sensor(sensor)
                            .condition(condition)
                            .build();

            scenario.getConditions().add(scenarioCondition);
        }

        /*
         * ACTIONS
         */
        for (DeviceActionAvro actionAvro : event.getActions()) {
            String sensorId =
                    actionAvro.getSensorId().toString();

            Sensor sensor = getSensorOrThrow(
                    sensors,
                    sensorId
            );

            Action action = Action.builder()
                    .type(ActionType.valueOf(
                            actionAvro.getActionType().name()))
                    .value(actionAvro.getValue())
                    .build();

            action = actionRepository.save(action);

            ScenarioAction scenarioAction =
                    ScenarioAction.builder()
                            .id(
                                    new ScenarioActionId(
                                            scenario.getId(),
                                            sensor.getId(),
                                            action.getId()
                                    )
                            )
                            .scenario(scenario)
                            .sensor(sensor)
                            .action(action)
                            .build();

            scenario.getActions().add(scenarioAction);
        }

        scenarioRepository.save(scenario);
    }

    @Transactional
    public void removeScenario(
            ScenarioRemovedEventAvro event,
            String hubId
    ) {
        scenarioRepository.findByHubIdAndName(
                hubId,
                event.getName().toString()
        ).ifPresent(scenarioRepository::delete);
    }

    public List<Scenario> getScenarios(String hubId) {
        return scenarioRepository.findByHubId(hubId);
    }

    private Set<String> collectSensorIds(
            ScenarioAddedEventAvro event
    ) {

        Set<String> conditionSensorIds =
                event.getConditions()
                        .stream()
                        .map(condition ->
                                condition.getSensorId().toString()
                        )
                        .collect(Collectors.toSet());

        Set<String> actionSensorIds =
                event.getActions()
                        .stream()
                        .map(action ->
                                action.getSensorId().toString()
                        )
                        .collect(Collectors.toSet());

        conditionSensorIds.addAll(actionSensorIds);

        return conditionSensorIds;
    }

    private Sensor getSensorOrThrow(
            Map<String, Sensor> sensors,
            String sensorId
    ) {
        Sensor sensor = sensors.get(sensorId);

        if (sensor == null) {
            throw new IllegalArgumentException(
                    "Sensor not found: " + sensorId
            );
        }
        return sensor;
    }

    private Integer extractConditionValue(
            ScenarioConditionAvro condition
    ) {
        Object value = condition.getValue();

        if (value instanceof Integer i) {
            return i;
        }

        if (value instanceof Boolean b) {
            return b ? 1 : 0;
        }

        throw new IllegalArgumentException(
                "Unsupported condition value type: "
                        + value.getClass().getName()
        );
    }
}