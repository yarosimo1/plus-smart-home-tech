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

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Transactional
    public void addSensor(DeviceAddedEventAvro event, String hubId) {

        if (sensorRepository.existsById(event.getId().toString())) {
            return;
        }

        Sensor sensor = Sensor.builder()
                .id(event.getId().toString())
                .hubId(hubId)
                .build();

        sensorRepository.save(sensor);
    }

    @Transactional
    public void removeSensor(DeviceRemovedEventAvro event) {
        sensorRepository.deleteById(event.getId().toString());
    }

    @Transactional
    public void addScenario(ScenarioAddedEventAvro event, String hubId) {

        Scenario scenario = scenarioRepository
                .findByHubIdAndName(
                        hubId,
                        event.getName().toString()
                )
                .orElseGet(() -> Scenario.builder()
                        .hubId(hubId)
                        .name(event.getName().toString())
                        .build());

        scenario.getConditions().clear();
        scenario.getActions().clear();

        for (ScenarioConditionAvro conditionAvro : event.getConditions()) {

            Condition condition = Condition.builder()
                    .type(ConditionType.valueOf(
                            conditionAvro.getConditionType().name()
                    ))
                    .operation(ConditionOperation.valueOf(
                            conditionAvro.getOperation().name()
                    ))
                    .value(extractConditionValue(conditionAvro))
                    .build();

            condition = conditionRepository.save(condition);

            ScenarioCondition scenarioCondition =
                    ScenarioCondition.builder()
                            .scenario(scenario)
                            .sensorId(conditionAvro.getSensorId().toString())
                            .condition(condition)
                            .build();

            scenario.getConditions().add(scenarioCondition);
        }

        for (DeviceActionAvro actionAvro : event.getActions()) {

            Action action = Action.builder()
                    .type(ActionType.valueOf(
                            actionAvro.getActionType().name()
                    ))
                    .value(actionAvro.getValue())
                    .build();

            action = actionRepository.save(action);

            ScenarioAction scenarioAction =
                    ScenarioAction.builder()
                            .scenario(scenario)
                            .sensorId(actionAvro.getSensorId().toString())
                            .action(action)
                            .build();

            scenario.getActions().add(scenarioAction);
        }

        scenarioRepository.save(scenario);
    }

    @Transactional
    public void removeScenario(ScenarioRemovedEventAvro event, String hubId) {

        scenarioRepository.findByHubIdAndName(
                hubId,
                event.getName().toString()
        ).ifPresent(scenarioRepository::delete);
    }

    public List<Scenario> getScenarios(String hubId) {
        return scenarioRepository.findByHubId(hubId);
    }

    private Integer extractConditionValue(ScenarioConditionAvro condition) {

        Object value = condition.getValue();

        if (value instanceof Integer i) {
            return i;
        }

        if (value instanceof Boolean b) {
            return b ? 1 : 0;
        }

        return null;
    }
}