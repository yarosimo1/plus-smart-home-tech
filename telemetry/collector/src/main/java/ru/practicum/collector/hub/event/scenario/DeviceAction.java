package ru.practicum.collector.hub.event.scenario;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

@Getter
@Setter
public class DeviceAction {
    private String sensorId;
    private ActionTypeAvro type;
    private Integer value;
}
