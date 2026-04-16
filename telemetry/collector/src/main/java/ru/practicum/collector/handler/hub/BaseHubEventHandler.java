package ru.practicum.collector.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.handler.KafkaEventProducer;
import ru.practicum.collector.model.hub.event.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    private final KafkaEventProducer kafkaEventProducer;
    private final String TELEMETRY_HUBS_V1 = "telemetry.hubs.v1";

    public BaseHubEventHandler(KafkaEventProducer kafkaEventProducer) {
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public void handle(HubEvent event) {
        SpecificRecordBase payload = mapToAvro(event);

        HubEventAvro avro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        kafkaEventProducer.send(
                TELEMETRY_HUBS_V1,
                event.getHubId(),
                avro
        );
    }

    public abstract T mapToAvro(HubEvent event);
}
