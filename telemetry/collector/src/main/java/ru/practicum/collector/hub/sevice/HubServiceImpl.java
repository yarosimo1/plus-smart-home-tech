package ru.practicum.collector.hub.sevice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.collector.EventCollectorProducer;
import ru.practicum.collector.sensor.event.SensorEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubServiceImpl implements HubService {
    private final EventCollectorProducer producer;

    @Override
    public void send(SensorEvent event) {
        log.info("Отпарвка сообщения хаба");
        producer.send(event.getHubId(), event);
    }
}