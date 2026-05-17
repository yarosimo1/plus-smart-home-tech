package ru.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component(value = "TEMPERATURE_SENSOR")
public class TemperatureSensorEventHandler extends BaseSensorEventHandler<TemperatureSensorAvro> {
    public TemperatureSensorEventHandler(KafkaEventProducer kafkaEventProducer, KafkaConfigProperties topics) {
        super(kafkaEventProducer, topics);
    }

    @Override
    public TemperatureSensorAvro mapToAvro(SensorEventProto event) {
        TemperatureSensorProto _event = event.getTemperatureSensor();
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(_event.getTemperatureC())
                .setTemperatureF(_event.getTemperatureF())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }
}