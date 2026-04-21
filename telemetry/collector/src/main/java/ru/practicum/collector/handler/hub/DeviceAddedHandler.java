package ru.practicum.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component(value = "DEVICE_ADDED")
public class DeviceAddedHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedHandler(KafkaEventProducer kafkaEventProducer, KafkaConfigProperties topics) {
        super(kafkaEventProducer, topics);
    }

    @Override
    public DeviceAddedEventAvro mapToAvro(HubEventProto event) {
        DeviceAddedEventProto _event = event.getDeviceAdded();

        return DeviceAddedEventAvro.newBuilder()
                .setDeviceType(DeviceTypeAvro.valueOf(_event.getType().name()))
                .setId(_event.getId())
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }
}