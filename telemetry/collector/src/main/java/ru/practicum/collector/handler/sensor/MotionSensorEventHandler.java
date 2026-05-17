package ru.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component(value = "MOTION_SENSOR")
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {
    public MotionSensorEventHandler(KafkaEventProducer kafkaEventProducer, KafkaConfigProperties topics) {
        super(kafkaEventProducer, topics);
    }

    @Override
    public MotionSensorAvro mapToAvro(SensorEventProto event) {
        MotionSensorProto _event = event.getMotionSensor();
        return MotionSensorAvro.newBuilder()
                .setMotion(_event.getMotion())
                .setLinkQuality(_event.getLinkQuality())
                .setVoltage(_event.getVoltage())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }
}
