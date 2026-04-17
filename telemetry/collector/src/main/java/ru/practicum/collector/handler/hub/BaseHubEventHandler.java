package ru.practicum.collector.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.practicum.collector.model.hub.event.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    private final KafkaEventProducer kafkaEventProducer;
    private final KafkaConfigProperties topics;

    public BaseHubEventHandler(KafkaEventProducer kafkaEventProducer, KafkaConfigProperties topics) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.topics = topics;
    }

    @Override
    public void handle(HubEvent event) {
        T payload = mapToAvro(event);

        HubEventAvro avro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        kafkaEventProducer.send(
                topics.getTopics().getHubs(),
                event.getHubId(),
                avro
        );
    }

    public abstract T mapToAvro(HubEvent event);
}