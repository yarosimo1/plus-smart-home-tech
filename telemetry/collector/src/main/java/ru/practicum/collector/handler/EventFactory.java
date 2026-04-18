package ru.practicum.collector.handler;

import org.springframework.stereotype.Component;
import ru.practicum.collector.handler.hub.HubEventHandler;
import ru.practicum.collector.handler.sensor.SensorEventHandler;
import ru.practicum.collector.model.hub.event.HubEventType;
import ru.practicum.collector.model.sensor.event.SensorEventType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventFactory {
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public EventFactory(List<SensorEventHandler> sensorEventHandlers, Set<HubEventHandler> hubEventHandlerSet) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlerSet.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    public SensorEventHandler getSensorEvetnHandler(SensorEventType eventType) {
        return sensorEventHandlers.get(eventType);
    }

    public HubEventHandler getHubEventHandler(HubEventType eventType) {
        return hubEventHandlers.get(eventType);
    }
}