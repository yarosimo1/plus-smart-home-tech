package ru.practicum.collector.hub.event.device;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.collector.hub.event.HubEvent;
import ru.practicum.collector.hub.event.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {
    private String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED_EVENT;
    }
}
