package ru.yandex.practicum.service.components;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.model.ConditionType;
import ru.yandex.practicum.service.ValueExtractor;

@Component
public class HumidityExtractor implements ValueExtractor {

    @Override
    public int extract(Object data) {
        if (data instanceof ClimateSensorAvro c) {
            return c.getHumidity();
        }
        return 0;
    }

    @Override
    public ConditionType getType() {
        return ConditionType.HUMIDITY;
    }
}
