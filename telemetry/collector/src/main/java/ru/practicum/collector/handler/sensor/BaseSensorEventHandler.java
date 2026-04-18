package ru.practicum.collector.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.practicum.collector.model.sensor.event.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    private final KafkaEventProducer kafkaEventProducer;
    private final KafkaConfigProperties topics;


    public BaseSensorEventHandler(KafkaEventProducer kafkaEventProducer, KafkaConfigProperties topics) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.topics = topics;
    }

    @Override
    public void handle(SensorEvent event) {
        T payload = mapToAvro(event);

        SensorEventAvro avro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        kafkaEventProducer.send(
                topics.getTopics().getSensors(),
                event.getHubId(),
                avro
        );
    }

    public abstract T mapToAvro(SensorEvent event);
}