package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.*;

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

        scenario.getConditions().clear();
        scenario.getActions().clear();

        scenario = scenarioRepository.save(scenario);

        processConditions(scenario, event);
        processActions(scenario, event);
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

        // проверка: уже существует
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

        // удаляем связи с условиями
        scenarioConditionRepository.deleteAllBySensorId(sensorId);

        // удаляем связи с действиями
        scenarioActionRepository.deleteAllBySensorId(sensorId);

        // удаляем сам сенсор
        sensorRepository.deleteById(sensorId);
    }


    private void processConditions(Scenario scenario,
                                   ScenarioAddedEventAvro event) {

        for (var c : event.getConditions()) {

            Sensor sensor = sensorRepository.findById(c.getSensorId())
                    .orElseThrow(() -> new RuntimeException("Sensor not found"));

            Condition condition = new Condition();
            condition.setType(c.getConditionType().toString());
            condition.setOperation(c.getOperation().toString());
            condition.setValue(c.getValue());

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
                    .orElseThrow(() -> new RuntimeException("Sensor not found"));

            Action action = new Action();
            action.setType(a.getActionType().toString());
            action.setValue(a.getValue());

            action = actionRepository.save(action);

            ScenarioAction sa = new ScenarioAction();
            sa.setScenario(scenario);
            sa.setSensor(sensor);
            sa.setAction(action);

            scenario.getActions().add(sa);
        }
    }
}