package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.producer.SnapshotProducer;

@Service
@RequiredArgsConstructor
public class AggregationService {

    private final SnapshotService snapshotService;
    private final SnapshotProducer producer;

    public void handle(SensorEventAvro event) {

        snapshotService.updateState(event)
                .ifPresent(producer::send);
    }
}