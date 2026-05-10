package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Service
@RequiredArgsConstructor
public class HubEventService {

    private final ScenarioService scenarioService;

    @Transactional
    public void handle(HubEventAvro event) {

        switch (event.getPayload()) {

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

            default -> throw new IllegalArgumentException(
                    "Unknown event type: " + event.getPayload().getClass()
            );
        }
    }
}