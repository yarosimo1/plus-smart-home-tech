package ru.practicum.collector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.collector.hub.event.HubEvent;
import ru.practicum.collector.hub.mapper.HubMapper;
import ru.practicum.collector.sensor.event.SensorEvent;
import ru.practicum.collector.sensor.mapper.SensorMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventRouter {
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    public void routeSensorEvent(SensorEvent event) {
        log.info("Обработка сообщения роутером для сенсоров");
        SpecificRecordBase avro = SensorMapper.toAvro(event);

        kafkaTemplate.send(
                CollectorTopics.TELEMETRY_SENSOR_V1,
                event.getHubId(),   // ключ
                avro
        );
    }

    public void routeHubEvent(HubEvent event) {
        log.info("Обработка сообщения роутером для хабов");
        SpecificRecordBase avro = HubMapper.toAvro(event);

        kafkaTemplate.send(
                CollectorTopics.TELEMETRY_HUBS_V1,
                event.getHubId(),
                avro
        );
    }
}
