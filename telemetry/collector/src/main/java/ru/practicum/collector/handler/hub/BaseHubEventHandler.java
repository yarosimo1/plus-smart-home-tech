package ru.practicum.collector.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.practicum.collector.mapper.ProtoTimestampMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public abstract class BaseHubEventHandler<T extends SpecificRecordBase>
        implements HubEventHandler {

    private final KafkaEventProducer kafkaEventProducer;
    private final KafkaConfigProperties properties;

    protected BaseHubEventHandler(
            KafkaEventProducer kafkaEventProducer,
            KafkaConfigProperties properties
    ) {

        this.kafkaEventProducer = kafkaEventProducer;
        this.properties = properties;
    }

    @Override
    public void handle(HubEventProto event) {

        T payload = mapToAvro(event);

        HubEventAvro avro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(
                        ProtoTimestampMapper.toInstant(
                                event.getTimestamp()
                        )
                )
                .setPayload(payload)
                .build();

        kafkaEventProducer.send(
                properties.getTopics().getHubs(),
                event.getHubId(),
                avro
        );
    }

    protected abstract T mapToAvro(HubEventProto event);
}