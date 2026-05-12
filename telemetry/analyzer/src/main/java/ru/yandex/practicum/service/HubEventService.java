package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventService {
    private final ScenarioService scenarioService;

    @Transactional
    public void handle(HubEventAvro event) {
        Object payload = event.getPayload();

        switch (payload) {
            case DeviceAddedEventAvro added -> scenarioService.addSensor(
                    added,
                    event.getHubId()
            );

            case DeviceRemovedEventAvro removed -> scenarioService.removeSensor(
                    removed
            );

            case ScenarioAddedEventAvro added -> scenarioService.addScenario(
                    added,
                    event.getHubId()
            );

            case ScenarioRemovedEventAvro removed -> scenarioService.removeScenario(
                    removed,
                    event.getHubId()
            );

            default -> log.warn(
                    "Unknown hub event type: {}, hubId={}",
                    payload.getClass().getName(),
                    event.getHubId()
            );
        }
    }
}