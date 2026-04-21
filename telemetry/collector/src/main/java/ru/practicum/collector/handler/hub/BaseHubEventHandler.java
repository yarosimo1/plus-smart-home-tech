package ru.practicum.collector.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    private final KafkaEventProducer kafkaEventProducer;
    private final KafkaConfigProperties topics;

    public BaseHubEventHandler(KafkaEventProducer kafkaEventProducer, KafkaConfigProperties topics) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.topics = topics;
    }

    @Override
    public void handle(HubEventProto event) {
        T payload = mapToAvro(event);

        HubEventAvro avro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();

        kafkaEventProducer.send(
                topics.getTopics().getHubs(),
                event.getHubId(),
                avro
        );
    }

    public abstract T mapToAvro(HubEventProto event);
}