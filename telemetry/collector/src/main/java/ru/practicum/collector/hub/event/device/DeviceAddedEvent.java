package ru.practicum.collector.hub.event.device;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.collector.hub.event.HubEvent;
import ru.practicum.collector.hub.event.HubEventType;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {
    private String id;
    private DeviceTypeAvro deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED_EVENT;
    }
}
