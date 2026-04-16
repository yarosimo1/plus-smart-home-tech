package ru.practicum.collector.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.handler.KafkaEventProducer;
import ru.practicum.collector.model.sensor.event.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    private final KafkaEventProducer kafkaEventProducer;
    private final String TELEMETRY_SENSOR_V1 = "telemetry.sensors.v1";

    public BaseSensorEventHandler(KafkaEventProducer kafkaEventProducer) {
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public void handle(SensorEvent event) {
        SpecificRecordBase payload = mapToAvro(event);

        SensorEventAvro avro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        kafkaEventProducer.send(
                TELEMETRY_SENSOR_V1,
                event.getHubId(),
                avro
        );
    }

    public abstract T mapToAvro(SensorEvent event);
}