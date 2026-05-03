package ru.yandex.practicum.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioEngine {
    private final ScenarioRepository scenarioRepository;
    private final ConditionEvaluator conditionEvaluator;
    private final ActionExecutor actionExecutor;

    @Transactional(readOnly = true)
    public void process(SensorsSnapshotAvro snapshot) {

        log.info("SNAPSHOT RECEIVED: hubId={}", snapshot.getHubId());
        log.info("Sensors in snapshot: {}", snapshot.getSensorsState().keySet());

        List<Scenario> scenarios =
                scenarioRepository.findByHubId(snapshot.getHubId());

        log.info("FOUND {} scenarios", scenarios.size());

        List<DeviceActionRequest> requests = new ArrayList<>();

        for (Scenario scenario : scenarios) {

            log.info("CHECKING SCENARIO: {}", scenario.getName());
            log.info("Conditions count: {}", scenario.getConditions().size());

            boolean triggered = scenario.getConditions().stream()
                    .peek(c -> log.info(
                            "Condition -> sensorId={}, type={}, op={}, value={}",
                            c.getSensor().getId(),
                            c.getCondition().getType(),
                            c.getCondition().getOperation(),
                            c.getCondition().getValue()
                    ))
                    .allMatch(c -> {
                        boolean result = conditionEvaluator.evaluate(snapshot, c);
                        log.info("Condition result = {}", result);
                        return result;
                    });

            log.info("SCENARIO [{}] triggered = {}", scenario.getName(), triggered);

            if (triggered) {
                log.info("ADDING ACTIONS for scenario={}", scenario.getName());
                requests.addAll(actionExecutor.buildRequests(snapshot, scenario));
            }
        }

        log.info("TOTAL ACTION REQUESTS: {}", requests.size());

        actionExecutor.executeAll(requests);
    }
}
