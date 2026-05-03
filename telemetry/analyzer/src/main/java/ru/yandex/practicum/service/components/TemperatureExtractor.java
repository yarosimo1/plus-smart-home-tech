package ru.yandex.practicum.service.components;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.ConditionType;
import ru.yandex.practicum.service.ValueExtractor;

@Component
public class TemperatureExtractor implements ValueExtractor {

    @Override
    public int extract(Object data) {
        return switch (data) {
            case ClimateSensorAvro c -> c.getTemperatureC();
            case TemperatureSensorAvro t -> t.getTemperatureC();
            default -> 0;
        };
    }

    @Override
    public ConditionType getType() {
        return ConditionType.TEMPERATURE;
    }
}