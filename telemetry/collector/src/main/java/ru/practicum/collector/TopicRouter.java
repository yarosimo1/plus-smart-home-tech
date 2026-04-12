package ru.practicum.collector;

import org.springframework.stereotype.Component;
import ru.practicum.collector.hub.event.HubEvent;
import ru.practicum.collector.sensor.event.SensorEvent;

import java.util.Map;

@Component
public class TopicRouter {
    private final Map<Class<?>, String> routingMap = Map.of(
            HubEvent.class, CollectorTopics.TELEMETRY_HUBS_V1,
            SensorEvent.class, CollectorTopics.TELEMETRY_SENSOR_V1
    );

    public String resolveTopic(Object event) {

        if (event instanceof HubEvent) {
            return CollectorTopics.TELEMETRY_HUBS_V1;
        }

        if (event instanceof SensorEvent) {
            return CollectorTopics.TELEMETRY_SENSOR_V1;
        }

        throw new IllegalArgumentException(
                "Unknown event type: " + event.getClass()
        );
    }
}
