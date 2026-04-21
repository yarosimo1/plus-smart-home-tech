package ru.practicum.collector.handler;

import org.springframework.stereotype.Component;
import ru.practicum.collector.handler.hub.HubEventHandler;
import ru.practicum.collector.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventFactory {
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public EventFactory(Set<SensorEventHandler> sensorEventHandlers, Set<HubEventHandler> hubEventHandlerSet) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlerSet.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    public SensorEventHandler getSensorEvetnHandler(SensorEventProto.PayloadCase eventType) {
        return sensorEventHandlers.get(eventType);
    }

    public HubEventHandler getHubEventHandler(HubEventProto.PayloadCase eventType) {
        return hubEventHandlers.get(eventType);
    }
}