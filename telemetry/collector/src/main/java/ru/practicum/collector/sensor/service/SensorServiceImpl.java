package ru.practicum.collector.sensor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.collector.EventCollectorProducer;
import ru.practicum.collector.sensor.event.SensorEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {
    private final EventCollectorProducer producer;

    @Override
    public void send(SensorEvent event) {
        log.info("Отпарвка сообщения сенсора");
        producer.send(event.getId(), event);
    }
}