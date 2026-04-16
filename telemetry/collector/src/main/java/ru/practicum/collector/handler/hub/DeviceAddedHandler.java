package ru.practicum.collector.handler.hub;

import org.springframework.context.annotation.Configuration;
import ru.practicum.collector.handler.KafkaEventProducer;
import ru.practicum.collector.model.hub.event.HubEvent;
import ru.practicum.collector.model.hub.event.HubEventType;
import ru.practicum.collector.model.hub.event.device.DeviceAddedEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Configuration(value = "DEVICE_ADDED")
public class DeviceAddedHandler extends BaseHubEventHandler<DeviceAddedEventAvro>{
    public DeviceAddedHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public DeviceAddedEventAvro mapToAvro(HubEvent event) {
        DeviceAddedEvent _event = (DeviceAddedEvent) event;
        return DeviceAddedEventAvro.newBuilder()
                .setDeviceType(DeviceTypeAvro.valueOf(_event.getDeviceType().name()))
                .setId(_event.getId())
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }
}
