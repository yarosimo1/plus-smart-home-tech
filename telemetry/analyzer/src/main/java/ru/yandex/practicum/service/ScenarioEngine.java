package ru.yandex.practicum.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScenarioEngine {

    private final ScenarioRepository scenarioRepository;
    private final ConditionEvaluator conditionEvaluator;
    private final ActionExecutor actionExecutor;

    @Transactional(readOnly = true)
    public void process(SensorsSnapshotAvro snapshot) {

        List<Scenario> scenarios =
                scenarioRepository.findByHubId(snapshot.getHubId());

        for (Scenario scenario : scenarios) {

            boolean triggered = scenario.getConditions().stream()
                    .allMatch(c -> conditionEvaluator.evaluate(snapshot, c));

            if (triggered) {
                actionExecutor.execute(snapshot, scenario);
            }
        }
    }
}
