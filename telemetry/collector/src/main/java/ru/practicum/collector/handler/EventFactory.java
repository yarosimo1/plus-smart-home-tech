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

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler>
            sensorEventHandlers;

    private final Map<HubEventProto.PayloadCase, HubEventHandler>
            hubEventHandlers;

    public EventFactory(
            Set<SensorEventHandler> sensorHandlers,
            Set<HubEventHandler> hubHandlers
    ) {

        this.sensorEventHandlers = sensorHandlers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        SensorEventHandler::getMessageType,
                        Function.identity()
                ));

        this.hubEventHandlers = hubHandlers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        HubEventHandler::getMessageType,
                        Function.identity()
                ));
    }

    public SensorEventHandler getSensorEventHandler(
            SensorEventProto.PayloadCase payloadCase
    ) {
        return sensorEventHandlers.get(payloadCase);
    }

    public HubEventHandler getHubEventHandler(
            HubEventProto.PayloadCase payloadCase
    ) {
        return hubEventHandlers.get(payloadCase);
    }
}