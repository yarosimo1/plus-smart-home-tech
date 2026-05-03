package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    @Transactional
    public void handleScenarioAdded(String hubId, ScenarioAddedEventAvro event) {

        Scenario scenario = scenarioRepository
                .findByHubIdAndName(hubId, event.getName())
                .orElseGet(Scenario::new);

        scenario.setHubId(hubId);
        scenario.setName(event.getName());

        scenario = scenarioRepository.save(scenario);

        // чистим старые связи
        scenarioConditionRepository.deleteAllByScenarioId(scenario.getId());
        scenarioActionRepository.deleteAllByScenarioId(scenario.getId());

        scenario.getConditions().clear();
        scenario.getActions().clear();

        processConditions(scenario, event);
        processActions(scenario, event);

        scenarioRepository.save(scenario);
    }

    @Transactional
    public void handleScenarioRemoved(String hubId,
                                      ScenarioRemovedEventAvro event) {

        scenarioRepository.findByHubIdAndName(hubId, event.getName())
                .ifPresent(scenarioRepository::delete);
    }

    @Transactional
    public void handleDeviceAdded(String hubId,
                                  DeviceAddedEventAvro event) {

        String sensorId = event.getId();

        if (sensorRepository.existsById(sensorId)) {
            return;
        }

        Sensor sensor = new Sensor();
        sensor.setId(sensorId);
        sensor.setHubId(hubId);

        sensorRepository.save(sensor);
    }

    @Transactional
    public void handleDeviceRemoved(String hubId,
                                    DeviceRemovedEventAvro event) {

        String sensorId = event.getId();

        scenarioConditionRepository.deleteAllBySensorId(sensorId);
        scenarioActionRepository.deleteAllBySensorId(sensorId);

        sensorRepository.findById(sensorId)
                .filter(sensor -> sensor.getHubId().equals(hubId))
                .ifPresent(sensorRepository::delete);
    }

    private void processConditions(Scenario scenario,
                                   ScenarioAddedEventAvro event) {

        for (var c : event.getConditions()) {

            Sensor sensor = sensorRepository.findById(c.getSensorId())
                    .orElseThrow(() -> new RuntimeException("Sensor not found: " + c.getSensorId()));

            Condition condition = new Condition();
            condition.setType(ConditionType.valueOf(c.getConditionType().name()));
            condition.setOperation(Operation.valueOf(c.getOperation().name()));
            condition.setValue(normalizeValue(c.getValue()));

            condition = conditionRepository.save(condition);

            ScenarioCondition sc = new ScenarioCondition();
            sc.setScenario(scenario);
            sc.setSensor(sensor);
            sc.setCondition(condition);

            scenario.getConditions().add(sc);
        }
    }

    private void processActions(Scenario scenario,
                                ScenarioAddedEventAvro event) {

        for (var a : event.getActions()) {

            Sensor sensor = sensorRepository.findById(a.getSensorId())
                    .orElseThrow(() -> new RuntimeException("Sensor not found: " + a.getSensorId()));

            Action action = new Action();
            action.setType(ActionType.valueOf(a.getActionType().name()));
            action.setValue(normalizeValue(a.getValue()));

            action = actionRepository.save(action);

            ScenarioAction sa = new ScenarioAction();
            sa.setScenario(scenario);
            sa.setSensor(sensor);
            sa.setAction(action);

            scenario.getActions().add(sa);
        }
    }

    private Integer normalizeValue(Object value) {
        if (value instanceof Boolean b) {
            return b ? 1 : 0;
        }
        if (value instanceof Integer i) {
            return i;
        }
        throw new IllegalArgumentException("Unsupported value type: " + value);
    }
}