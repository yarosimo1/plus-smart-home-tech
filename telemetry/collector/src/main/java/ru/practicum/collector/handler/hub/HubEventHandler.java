package ru.practicum.collector.handler.hub;

import ru.practicum.collector.model.hub.event.HubEvent;
import ru.practicum.collector.model.hub.event.HubEventType;

public interface HubEventHandler {
    HubEventType getMessageType();

    void handle(HubEvent event);
}
