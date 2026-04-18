package ru.practicum.collector.handler.sensor;

import ru.practicum.collector.model.sensor.event.SensorEvent;
import ru.practicum.collector.model.sensor.event.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getMessageType();

    void handle(SensorEvent event);
}
