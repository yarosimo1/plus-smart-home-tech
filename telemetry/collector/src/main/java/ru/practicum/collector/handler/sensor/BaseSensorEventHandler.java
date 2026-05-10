package ru.practicum.collector.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.practicum.collector.mapper.ProtoTimestampMapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public abstract class BaseSensorEventHandler<T extends SpecificRecordBase>
        implements SensorEventHandler {

    private final KafkaEventProducer kafkaEventProducer;
    private final KafkaConfigProperties properties;

    protected BaseSensorEventHandler(
            KafkaEventProducer kafkaEventProducer,
            KafkaConfigProperties properties
    ) {

        this.kafkaEventProducer = kafkaEventProducer;
        this.properties = properties;
    }

    @Override
    public void handle(SensorEventProto event) {

        T payload = mapToAvro(event);

        SensorEventAvro avro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(
                        ProtoTimestampMapper.toInstant(
                                event.getTimestamp()
                        )
                )
                .setPayload(payload)
                .build();

        kafkaEventProducer.send(
                properties.getTopics().getSensors(),
                event.getHubId(),
                avro
        );
    }

    protected abstract T mapToAvro(SensorEventProto event);
}