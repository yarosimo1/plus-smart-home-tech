package ru.practicum.collector.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    private final KafkaEventProducer kafkaEventProducer;
    private final KafkaConfigProperties topics;


    public BaseSensorEventHandler(KafkaEventProducer kafkaEventProducer, KafkaConfigProperties topics) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.topics = topics;
    }

    @Override
    public void handle(SensorEventProto event) {
        T payload = mapToAvro(event);

        SensorEventAvro avro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();

        kafkaEventProducer.send(
                topics.getTopics().getSensors(),
                event.getHubId(),
                avro
        );
    }

    public abstract T mapToAvro(SensorEventProto event);
}