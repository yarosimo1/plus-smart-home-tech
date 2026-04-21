package ru.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.practicum.collector.handler.kafka.KafkaEventProducer;
import ru.practicum.collector.handler.kafka.config.KafkaConfigProperties;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component(value = "SWITCH_SENSOR")
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorAvro> {
    public SwitchSensorEventHandler(KafkaEventProducer kafkaEventProducer, KafkaConfigProperties topics) {
        super(kafkaEventProducer, topics);
    }

    @Override
    public SwitchSensorAvro mapToAvro(SensorEventProto event) {
        SwitchSensorProto _event = event.getSwitchSensor();
        return SwitchSensorAvro.newBuilder()
                .setState(_event.getState())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }
}
