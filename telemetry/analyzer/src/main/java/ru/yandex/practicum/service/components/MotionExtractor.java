package ru.yandex.practicum.service.components;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.model.ConditionType;
import ru.yandex.practicum.service.ValueExtractor;

@Component
public class MotionExtractor implements ValueExtractor {

    @Override
    public int extract(Object data) {
        if (data instanceof MotionSensorAvro m) {
            return m.getMotion() ? 1 : 0;
        }
        return 0;
    }

    @Override
    public ConditionType getType() {
        return ConditionType.MOTION;
    }
}