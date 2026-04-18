package ru.practicum.collector;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.collector.handler.EventFactory;
import ru.practicum.collector.handler.hub.HubEventHandler;
import ru.practicum.collector.handler.sensor.SensorEventHandler;
import ru.practicum.collector.model.hub.event.HubEvent;
import ru.practicum.collector.model.sensor.event.SensorEvent;

@RestController
@RequestMapping("/events")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CollectorController {
    private final EventFactory eventFactory;

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.info("Получения контроллером собщения {}", event);
        HubEventHandler hubEventHandler =
                eventFactory.getHubEventHandler(event.getType());

        if (hubEventHandler == null) {
            throw new IllegalArgumentException("Unknown event type: " + event.getType());
        }

        hubEventHandler.handle(event);
    }

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("Получения контроллером собщения {}", event);
        SensorEventHandler sensorEventHandler =
                eventFactory.getSensorEvetnHandler(event.getType());

        if (sensorEventHandler == null) {
            throw new IllegalArgumentException("Unknown event type: " + event);
        }

        sensorEventHandler.handle(event);
    }
}