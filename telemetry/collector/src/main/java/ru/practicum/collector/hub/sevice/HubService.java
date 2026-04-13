package ru.practicum.collector.hub.sevice;

import ru.practicum.collector.hub.event.HubEvent;

public interface HubService {
    public void send(HubEvent event);
}
