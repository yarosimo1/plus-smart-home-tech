package ru.yandex.practicum.service.components;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.service.ValueExtractor;

@Component("LUMINOSITY")
public class LuminosityExtractor implements ValueExtractor {

    @Override
    public int extract(Object data) {
        if (data instanceof LightSensorAvro l) {
            return l.getLuminosity();
        }
        return 0;
    }
}