package ru.practicum.collector.sensor.service;

import ru.practicum.collector.sensor.event.SensorEvent;

public interface SensorService {
    void send(SensorEvent event);
}
