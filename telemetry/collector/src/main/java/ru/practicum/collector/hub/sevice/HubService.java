package ru.practicum.collector.hub.sevice;

import ru.practicum.collector.sensor.event.SensorEvent;

public interface HubService {
    public void send(SensorEvent event);
}
